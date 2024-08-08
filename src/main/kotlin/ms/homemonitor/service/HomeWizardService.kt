package ms.homemonitor.service

import ms.homemonitor.config.HomeWizardProperties
import ms.homemonitor.infra.homewizard.model.HomeWizardData
import ms.homemonitor.infra.homewizard.rest.HomeWizard
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.HomeWizardRepository
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

        val homeWizardData = getHomeWizardData()
        repository.storeDetailedMeasurement(homeWizardData)
        setMetrics(homeWizardData)
    }

    @Scheduled(cron = "0 0 * * * *")
    fun hourPowerMeasurement() {
        if (!homeWizardProperties.enabled)
            return
        val homeWizardData = getHomeWizardData()
        repository.storeHourlyMeasurement(homeWizardData)
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun dayPowerMeasurement() {
        if (!homeWizardProperties.enabled)
            return
        val homeWizardData = getHomeWizardData()
        repository.storeDailyMeasurement(homeWizardData)
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