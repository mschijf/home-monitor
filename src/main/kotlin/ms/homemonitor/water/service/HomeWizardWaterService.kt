package ms.homemonitor.water.service

import jakarta.transaction.Transactional
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.service.model.YearSummary
import ms.homemonitor.shared.summary.service.SummaryService
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.water.repository.model.WaterEntity
import ms.homemonitor.water.repository.WaterRepository
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class HomeWizardWaterService(
    private val waterRepository: WaterRepository,
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
            throw HomeMonitorException("Error while processing and storing HomeWizard data", e)
        }
    }

    private fun setMetrics(data: HomeWizardWaterData) {
        measurement.setDoubleGauge("homewizardWaterActiveLpm", data.activeLiterLpm.toDouble())
    }

    @Transactional
    fun cleanupOldData(keepDays: Long) {
        val beforeTime = LocalDate.now().minusDays(keepDays)
        val recordsToDelete = waterRepository.countRecordsBeforeTime(beforeTime.atStartOfDay())
        waterRepository.deleteDataBeforeTime(beforeTime.atStartOfDay())
        log.info("Deleted $recordsToDelete water records")
    }

}