package ms.homemonitor.water.service

import jakarta.transaction.Transactional
import ms.homemonitor.heath.repository.HeathRepository
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.service.SummaryService
import ms.homemonitor.shared.summary.service.model.YearSummary
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.water.repository.ShowerUsageRepository
import ms.homemonitor.water.repository.WaterRepository
import ms.homemonitor.water.repository.model.ShowerUsageEntity
import ms.homemonitor.water.repository.model.WaterEntity
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import ms.homemonitor.water.service.model.ShowerSession
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class HomeWizardWaterService(
    private val waterRepository: WaterRepository,
    private val heathRepository: HeathRepository,
    private val showerUsageRepository: ShowerUsageRepository,
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val measurement: MicroMeterMeasurement,
    private val summary: SummaryService,
    @Value("\${home-monitor.homewizard.initialWaterValue}") private val initialWaterValue: BigDecimal,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun getWaterYearSummary(): YearSummary {
        return summary.getSummary(waterRepository)
    }

    fun processMeasurement(persistentStore: Boolean) {
        try {
            val now = LocalDateTime.now()
            val homeWizardWaterData = homeWizardWaterClient.getHomeWizardWaterData()
            setMetrics(homeWizardWaterData)
            if (persistentStore) {
                waterRepository.saveAndFlush(
                    WaterEntity(
                        time = now,
                        waterM3 = homeWizardWaterData.totalLiterM3 + initialWaterValue,
                    )
                )
            }
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing and storing HomeWizard Water data", e)
        }
    }

    private fun setMetrics(data: HomeWizardWaterData) {
        measurement.setDoubleGauge("homewizardWaterActiveLpm", data.activeLiterLpm.toDouble())
    }

    fun getShowers(date: LocalDate): List<ShowerSession> {
        val from = date.atStartOfDay()
        val until = date.plusDays(1).atStartOfDay()
        val records = waterRepository.findByTimeBetweenOrderByTime(from, until)

        val flows = records
            .zipWithNext()
            .mapNotNull { (prev, curr) ->
                val prevVal = prev.waterM3 ?: return@mapNotNull null
                val currVal = curr.waterM3 ?: return@mapNotNull null
                Pair(curr.time, (currVal - prevVal).toDouble() * 1000.0)
            }

        val sessions = buildSessions(flows)

        // Eneco stores heat per hour; time = start of the hour (e.g. 07:00 covers 07:00–08:00)
        val heathRecords = heathRepository.findByTimeBetweenOrderByTime(from, until)

        return sessions.map { session ->
            val hourFrom = session.startTime.truncatedTo(ChronoUnit.HOURS)
            val hourUntil = session.endTime.truncatedTo(ChronoUnit.HOURS)
            val heatGJ = heathRecords
                .filter { it.time >= hourFrom && it.time <= hourUntil }
                .mapNotNull { it.deltaGJ?.toDouble() }
                .takeIf { it.isNotEmpty() }
                ?.sum()
                ?.let { Math.round(it * 10_000) / 10_000.0 }
            session.copy(heatGJ = heatGJ)
        }.filter { it.heatGJ != null && it.heatGJ > 0 }
    }

    private fun buildSessions(flows: List<Pair<LocalDateTime, Double>>): List<ShowerSession> {
        val minFlowLiters = 1.0
        val maxGapMinutes = 3L
        val minSessionLiters = 20.0

        val sessions = mutableListOf<ShowerSession>()
        var sessionStart: LocalDateTime? = null
        var sessionEnd: LocalDateTime? = null
        var sessionLiters = 0.0

        fun closeSession() {
            if (sessionStart != null && sessionLiters >= minSessionLiters) {
                sessions.add(ShowerSession(
                    startTime = sessionStart!!,
                    endTime = sessionEnd!!,
                    durationMinutes = ChronoUnit.MINUTES.between(sessionStart, sessionEnd).toInt(),
                    liters = Math.round(sessionLiters * 10) / 10.0,
                    heatGJ = null,
                ))
            }
            sessionStart = null
            sessionEnd = null
            sessionLiters = 0.0
        }

        for ((time, liters) in flows) {
            if (liters >= minFlowLiters) {
                if (sessionStart == null) {
                    sessionStart = time
                } else if (ChronoUnit.MINUTES.between(sessionEnd, time) > maxGapMinutes) {
                    closeSession()
                    sessionStart = time
                }
                sessionEnd = time
                sessionLiters += liters
            }
        }
        closeSession()

        return sessions
    }

    fun processShowerUsage(date: LocalDate) {
        val showers = getShowers(date)
        if (showers.isEmpty()) {
            showerUsageRepository.saveAndFlush(
                ShowerUsageEntity(
                    startTime = date.atStartOfDay(),
                    endTime = date.atStartOfDay(),
                    durationMinutes = 0,
                    liters = 0.0,
                    heatGJ = 0.0,
                )
            )
        } else {
            showers.forEach { shower ->
                showerUsageRepository.saveAndFlush(
                    ShowerUsageEntity(
                        startTime = shower.startTime,
                        endTime = shower.endTime,
                        durationMinutes = shower.durationMinutes,
                        liters = shower.liters,
                        heatGJ = shower.heatGJ ?: 0.0,
                    )
                )
            }
        }
        log.info("Shower usage for $date: ${showers.size} showers saved")
    }

    fun initShowers(fromDay: LocalDate) {
        val now = LocalDate.now()
        var current = fromDay
        while (current <= now) {
            processShowerUsage(current)
            current = current.plusDays(1)
        }
    }

    @Transactional
    fun cleanupOldData(keepDays: Long) {
        val beforeTime = LocalDate.now().minusDays(keepDays)
        val recordsToDelete = waterRepository.countRecordsBeforeTime(beforeTime.atStartOfDay())
        waterRepository.deleteDataBeforeTime(beforeTime.atStartOfDay())
        log.info("Deleted $recordsToDelete water records")
    }

}