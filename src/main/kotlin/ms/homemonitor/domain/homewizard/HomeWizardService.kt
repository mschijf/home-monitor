package ms.homemonitor.domain.homewizard

import ms.homemonitor.application.HomeMonitorException
import ms.homemonitor.domain.homewizard.model.HomeWizardData
import ms.homemonitor.domain.homewizard.repository.HomeWizardRepository
import ms.homemonitor.domain.homewizard.rest.HomeWizard
import ms.homemonitor.micrometer.MicroMeterMeasurement
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class HomeWizardService(
    private val homeWizard: HomeWizard,
    private val repository: HomeWizardRepository,
    private val measurement: MicroMeterMeasurement,
    private val homeWizardProperties: HomeWizardProperties,
) {

    fun getHomeWizardData(): HomeWizardData {
        val homeWizardEnergyData = homeWizard.getHomeWizardEnergyData()
        val homeWizardWaterData = homeWizard.getHomeWizardWaterData()
        return HomeWizardData(homeWizardEnergyData, homeWizardWaterData)
    }

    @Scheduled(fixedRate = 10_000)
    fun detailedPowerMeasurement() {
        if (!homeWizardProperties.enabled)
            return

        try {
            val homeWizardData = getHomeWizardData()
            repository.storeDetailedMeasurement(homeWizardData)
            setMetrics(homeWizardData)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing detailed HomeWizard data", e)
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    fun hourPowerMeasurement() {
        if (!homeWizardProperties.enabled)
            return
        try {
            val homeWizardData = getHomeWizardData()
            repository.storeHourlyMeasurement(homeWizardData)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing hour HomeWizard data", e)
        }
    }

    private fun setMetrics(data: HomeWizardData) {
        measurement.setDoubleGauge("homewizardPowerT1Kwh", data.energy.totalPowerImportT1Kwh.toDouble())
        measurement.setDoubleGauge("homewizardPowerT2Kwh", data.energy.totalPowerImportT2Kwh.toDouble())

        measurement.setDoubleGauge("homewizardActivePowerL1Watt", data.energy.activePowerL1Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL2Watt", data.energy.activePowerL2Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL3Watt", data.energy.activePowerL3Watt.toDouble())

        measurement.setDoubleGauge("homewizardWaterTotalM3", data.water.totalLiterM3.toDouble())
        measurement.setDoubleGauge("homewizardWaterActiveLpm", data.water.activeLiterLpm.toDouble())
    }
}