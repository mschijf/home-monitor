package ms.homemonitor.water.domain.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.domain.service.SummaryService
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.power.domain.rest.HomeWizard
import ms.homemonitor.water.data.model.WaterEntity
import ms.homemonitor.water.data.repository.WaterRepository
import ms.homemonitor.water.domain.model.HomeWizardWaterData
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class HomeWizardWaterService(
    private val homeWizard: HomeWizard,
    private val measurement: MicroMeterMeasurement,
    private val waterRepository: WaterRepository,
    private val summary: SummaryService,
    @Value("\${homewizard.enabled}") private val enabled: Boolean,
    @Value("\${homewizard.initialWaterValue}") private val initialWaterValue: BigDecimal,
) {

    @Scheduled(fixedRate = 10_000)
    fun detailedWaterMeasurement() {
        if (!enabled)
            return

        try {
            val homeWizardWaterData = homeWizard.getHomeWizardWaterData()
            setMetrics(homeWizardWaterData)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing detailed HomeWizard Water data", e)
        }
    }

    @Scheduled(cron = "0 * * * * *")
    fun minuteMeasurement() {
        if (!enabled)
            return

        try {
            val now = LocalDateTime.now()
            val homeWizardWaterData = homeWizard.getHomeWizardWaterData()
            waterRepository.saveAndFlush(
                WaterEntity(
                    time = now,
                    waterM3 = homeWizardWaterData.totalLiterM3 + initialWaterValue,
                )
            )

        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing and storing HomeWizard data", e)
        }
    }

    fun getWaterYearSummary(): YearSummary {
        return summary.getSummary(waterRepository)
    }

    private fun setMetrics(data: HomeWizardWaterData) {
        measurement.setDoubleGauge("homewizardWaterActiveLpm", data.activeLiterLpm.toDouble())
    }
}