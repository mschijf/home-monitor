package ms.homemonitor.water.domain.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.domain.service.SummaryService
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.water.data.model.WaterEntity
import ms.homemonitor.water.data.repository.WaterRepository
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class HomeWizardWaterScheduler(
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val measurement: MicroMeterMeasurement,
    private val waterRepository: WaterRepository,
    @Value("\${homewizard.enabled}") private val enabled: Boolean,
    @Value("\${homewizard.initialWaterValue}") private val initialWaterValue: BigDecimal,
) {

    @Scheduled(fixedRate = 10_000)
    fun detailedWaterMeasurement() {
        if (!enabled)
            return

        try {
            val homeWizardWaterData = homeWizardWaterClient.getHomeWizardWaterData()
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
            val homeWizardWaterData = homeWizardWaterClient.getHomeWizardWaterData()
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

    private fun setMetrics(data: HomeWizardWaterData) {
        measurement.setDoubleGauge("homewizardWaterActiveLpm", data.activeLiterLpm.toDouble())
    }
}