package ms.homemonitor.domain.homewizard

import ms.homemonitor.application.HomeMonitorException
import ms.homemonitor.domain.homewizard.model.HomeWizardData
import ms.homemonitor.repository.StandingsEntity
import ms.homemonitor.repository.StandingsRepository
import ms.homemonitor.domain.homewizard.rest.HomeWizard
import ms.homemonitor.micrometer.MicroMeterMeasurement
import ms.homemonitor.repository.WaterEntity
import ms.homemonitor.repository.WaterRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class HomeWizardService(
    private val homeWizard: HomeWizard,
    private val measurement: MicroMeterMeasurement,
    private val homeWizardProperties: HomeWizardProperties,
    private val standingsRepository: StandingsRepository,
    private val waterRepository: WaterRepository,
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
            setMetrics(homeWizardData)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing detailed HomeWizard data", e)
        }
    }

    @Scheduled(cron = "0 * * * * *")
    fun minuteMeasurement() {
        if (!homeWizardProperties.enabled)
            return
        try {
            val now = LocalDateTime.now()
            val homeWizardData = getHomeWizardData()
            standingsRepository.saveAndFlush(
                StandingsEntity(
                    time=now,
                    waterM3 = homeWizardData.water.totalLiterM3 + homeWizardProperties.initialWaterValue,
                    powerNormalKwh = homeWizardData.energy.totalPowerImportT2Kwh,
                    powerOffpeakKwh = homeWizardData.energy.totalPowerImportT1Kwh
                )
            )

            waterRepository.saveAndFlush(
                WaterEntity(
                    time=now,
                    waterM3 = homeWizardData.water.totalLiterM3 + homeWizardProperties.initialWaterValue,
                )
            )

        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing and storing HomeWizard data", e)
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