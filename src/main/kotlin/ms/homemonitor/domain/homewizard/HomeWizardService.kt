package ms.homemonitor.domain.homewizard

import ms.homemonitor.application.HomeMonitorException
import ms.homemonitor.domain.homewizard.model.HomeWizardData
import ms.homemonitor.repository.power.PowerEntity
import ms.homemonitor.repository.power.PowerRepository
import ms.homemonitor.domain.homewizard.rest.HomeWizard
import ms.homemonitor.domain.summary.SummaryService
import ms.homemonitor.domain.summary.model.YearSummary
import ms.homemonitor.micrometer.MicroMeterMeasurement
import ms.homemonitor.repository.water.WaterEntity
import ms.homemonitor.repository.water.WaterRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime


@Service
class HomeWizardService(
    private val homeWizard: HomeWizard,
    private val measurement: MicroMeterMeasurement,
    private val powerRepository: PowerRepository,
    private val waterRepository: WaterRepository,
    private val summary: SummaryService,
    @Value("\${homewizard.enabled}") private val enabled: Boolean,
    @Value("\${homewizard.initialWaterValue}") private val initialWaterValue: BigDecimal,
) {

    fun getHomeWizardData(): HomeWizardData {
        val homeWizardEnergyData = homeWizard.getHomeWizardEnergyData()
        val homeWizardWaterData = homeWizard.getHomeWizardWaterData()
        return HomeWizardData(homeWizardEnergyData, homeWizardWaterData)
    }

    @Scheduled(fixedRate = 10_000)
    fun detailedPowerMeasurement() {
        if (!enabled)
            return

        try {
            val homeWizardData = getHomeWizardData()
            setMetrics(homeWizardData)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing detailed HomeWizard data", e)
        }
    }

    @Scheduled(cron = "0 * * * * *")
    fun minuteMeasurement() {
        if (!enabled)
            return

        try {
            val now = LocalDateTime.now()
            val homeWizardData = getHomeWizardData()
            powerRepository.saveAndFlush(
                PowerEntity(
                    time=now,
                    powerNormalKwh = homeWizardData.energy.totalPowerImportT2Kwh,
                    powerOffpeakKwh = homeWizardData.energy.totalPowerImportT1Kwh
                )
            )

            waterRepository.saveAndFlush(
                WaterEntity(
                    time=now,
                    waterM3 = homeWizardData.water.totalLiterM3 + initialWaterValue,
                )
            )

        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing and storing HomeWizard data", e)
        }
    }

    fun getPowerYearSummary(): YearSummary {
        return summary.getSummary(powerRepository)
    }

    fun getWaterYearSummary(): YearSummary {
        return summary.getSummary(waterRepository)
    }


    private fun setMetrics(data: HomeWizardData) {
        measurement.setDoubleGauge("homewizardActivePowerL1Watt", data.energy.activePowerL1Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL2Watt", data.energy.activePowerL2Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL3Watt", data.energy.activePowerL3Watt.toDouble())
        measurement.setDoubleGauge("homewizardWaterActiveLpm", data.water.activeLiterLpm.toDouble())
    }
}