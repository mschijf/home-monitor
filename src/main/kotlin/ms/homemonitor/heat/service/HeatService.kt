package ms.homemonitor.heat.service

import jakarta.transaction.Transactional
import ms.homemonitor.heat.repository.HeatRepository
import ms.homemonitor.heat.repository.ManualHeatCorrectionRepository
import ms.homemonitor.heat.repository.model.HeatEntity
import ms.homemonitor.heat.repository.model.ManualHeatCorrectionEntity
import ms.homemonitor.heat.repository.model.ManualHeatCorrectionModel
import ms.homemonitor.heat.restclient.EnecoRestClient
import ms.homemonitor.shared.summary.service.SummaryService
import ms.homemonitor.shared.summary.service.model.YearSummary
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

@Service
class HeatService(
    private val heatRepository: HeatRepository,
    private val manualHeatCorrectionRepository: ManualHeatCorrectionRepository,
    private val enecoRestClient: EnecoRestClient,
    private val enecoStatsService: EnecoStatsService,
    private val summary: SummaryService,
    @Value("\${home-monitor.eneco.initialDate}") private val initialDate: LocalDateTime,
    @Value("\${home-monitor.eneco.initialHeatValue}") private val initialHeatValue: BigDecimal,
) {

    private val log = LoggerFactory.getLogger(HeatService::class.java)

    fun getYearSummary(): YearSummary {
        return summary.getSummary(heatRepository)
    }

    @Transactional
    fun setManualCorrection(manualStanding: ManualHeatCorrectionModel): Boolean {
        val lastHeat = heatRepository.getLastHeatEntity()
        if (okValue(manualStanding, lastHeat)) {
            manualHeatCorrectionRepository.saveAndFlush(
                ManualHeatCorrectionEntity(LocalDateTime.now(), manualStanding.heatGJ, lastHeat?.heatGJ)
            )
            updateEnecoData()
            return true
        } else {
            return false
        }
    }

    private fun okValue(manualStanding: ManualHeatCorrectionModel, lastHeat: HeatEntity?): Boolean {
        return if (lastHeat == null) {
            false
        } else {
            lastHeat.heatGJ!!
                .minus(manualStanding.heatGJ).toDouble()
                .absoluteValue < 1
        }
    }


    @Transactional
    fun processMeaurement() {
        val success = updateEnecoData()
        enecoStatsService.updateEnecoStats(success)
        if (!success) {
            processFailedUpdate()
        }
    }

    private fun updateEnecoData(): Boolean {
        val beginningOfLastDay = clearLastDay()
        val newHeatRecordList = getNewDataFromDate(beginningOfLastDay.toLocalDate())
        newHeatRecordList.forEach { heatRecord ->
            try {
                heatRepository.saveAndFlush(heatRecord)
            } catch (e: Exception) {
                log.info("Ignore exception ' ${e.message}' while updating record $heatRecord")
            }
        }

        return newHeatRecordList.isNotEmpty()
    }

    private fun lastRecord(): HeatEntity {
        return heatRepository.getLastHeatEntity() ?: HeatEntity(initialDate, BigDecimal.ZERO, initialHeatValue)
    }

    private fun clearLastDay(): LocalDateTime {
        val beginningOfLastDay = LocalDateTime.of(lastRecord().time.toLocalDate(), LocalTime.MIDNIGHT)
        heatRepository.deleteHeatEntitiesByTimeGreaterThanEqual(beginningOfLastDay)
        return beginningOfLastDay
    }

    private fun getNewDataFromDate(beginningOfLastDay: LocalDate): List<HeatEntity> {
        val freshDataList = enecoRestClient.getNewDataFromEneco(beginningOfLastDay).sortedBy { it.date }
        val newHeatRecordList = freshDataList
            .map{fresh ->
                HeatEntity(
                    time = fresh.date,
                    deltaGJ = fresh.totalUsedGigaJoule,
                    heatGJ = BigDecimal.ZERO
                )
            }
            .runningFold(lastRecord()) {acc, elt ->
                val correction = manualHeatCorrectionRepository.getLastCorrectionBetween(acc.time, elt.time)
                if (correction != null) {
                    HeatEntity(
                        time = elt.time,
                        deltaGJ = correction.heatGJ!!.minus(acc.heatGJ!!),
                        heatGJ = correction.heatGJ
                    )
                } else {
                    HeatEntity(
                        time = elt.time,
                        deltaGJ = elt.deltaGJ,
                        heatGJ = acc.heatGJ?.plus(elt.deltaGJ!!)
                    )
                }
            }
            .drop(1)

        return newHeatRecordList
    }

    private fun processFailedUpdate() {
        val lastUpdateTime = enecoStatsService.getLastSuccessfullUpdate()
        val now = LocalDateTime.now()
        val diff = ChronoUnit.HOURS.between(lastUpdateTime, now)
        if (diff > 12) {
            log.error("Last succesfull update more than $diff hours ago. Last succesfull one was at $lastUpdateTime")
        }
    }
}