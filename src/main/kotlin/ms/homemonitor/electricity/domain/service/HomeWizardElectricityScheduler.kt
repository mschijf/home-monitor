package ms.homemonitor.electricity.domain.service

import ms.homemonitor.electricity.data.model.ElectricityEntity
import ms.homemonitor.electricity.data.repository.ElectricityRepository
import ms.homemonitor.electricity.restclient.HomeWizardElectricityClient
import ms.homemonitor.electricity.restclient.model.HomeWizardElectricityData
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HomeWizardElectricityScheduler(
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val measurement: MicroMeterMeasurement,
    private val electricityRepository: ElectricityRepository,
    @Value("\${homewizard.enabled}") private val enabled: Boolean,
) {

    @Scheduled(fixedRate = 10_000)
    fun detailedElectricityMeasurement() {
        if (!enabled)
            return

        try {
            val homeWizardElectricityData = homeWizardElectricityClient.getHomeWizardElectricityData()
            setMetrics(homeWizardElectricityData)
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
            val homeWizardElectricityData = homeWizardElectricityClient.getHomeWizardElectricityData()
            electricityRepository.saveAndFlush(
                ElectricityEntity(
                    time = now,
                    powerNormalKwh = homeWizardElectricityData.totalPowerImportT2Kwh,
                    powerOffpeakKwh = homeWizardElectricityData.totalPowerImportT1Kwh
                )
            )

        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing and storing HomeWizard data", e)
        }
    }

    private fun setMetrics(data: HomeWizardElectricityData) {
        measurement.setDoubleGauge("homewizardActivePowerL1Watt", data.activePowerL1Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL2Watt", data.activePowerL2Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL3Watt", data.activePowerL3Watt.toDouble())
    }
}