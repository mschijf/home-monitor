package ms.homemonitor.electricity.domain.service

import ms.homemonitor.electricity.data.model.ElectricityEntity
import ms.homemonitor.electricity.data.repository.ElectricityRepository
import ms.homemonitor.electricity.restclient.HomeWizardElectricityClient
import ms.homemonitor.electricity.restclient.model.HomeWizardElectricityData
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.summary.domain.service.SummaryService
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HomeWizardElectricityService(
    private val electricityRepository: ElectricityRepository,
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val measurement: MicroMeterMeasurement,
    private val summary: SummaryService,
) {

    fun getElectricityYearSummary(): YearSummary {
        return summary.getSummary(electricityRepository)
    }

    fun processMeasurement(persistentStore: Boolean) {
        try {
            val now = LocalDateTime.now()
            val homeWizardElectricityData = homeWizardElectricityClient.getHomeWizardElectricityData()
            setMetrics(homeWizardElectricityData)
            if (persistentStore) {
                electricityRepository.saveAndFlush(
                    ElectricityEntity(
                        time = now,
                        powerNormalKwh = homeWizardElectricityData.totalPowerImportT2Kwh,
                        powerOffpeakKwh = homeWizardElectricityData.totalPowerImportT1Kwh
                    )
                )
            }

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