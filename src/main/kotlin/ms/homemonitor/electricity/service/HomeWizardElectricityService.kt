package ms.homemonitor.electricity.service

import jakarta.transaction.Transactional
import ms.homemonitor.electricity.repository.model.ElectricityEntity
import ms.homemonitor.electricity.repository.ElectricityRepository
import ms.homemonitor.electricity.restclient.HomeWizardElectricityClient
import ms.homemonitor.electricity.restclient.model.HomeWizardElectricityData
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.service.model.YearSummary
import ms.homemonitor.shared.summary.service.SummaryService
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class HomeWizardElectricityService(
    private val electricityRepository: ElectricityRepository,
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val measurement: MicroMeterMeasurement,
    private val summary: SummaryService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

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
            throw HomeMonitorException("Error while processing and storing HomeWizard Electricity data", e)
        }
    }

    private fun setMetrics(data: HomeWizardElectricityData) {
        measurement.setDoubleGauge("homewizardActivePowerL1Watt", data.activePowerL1Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL2Watt", data.activePowerL2Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL3Watt", data.activePowerL3Watt.toDouble())
    }

    @Transactional
    fun cleanupOldData(keepDays: Long) {
        val beforeTime = LocalDate.now().minusDays(keepDays)
        val recordsToDelete = electricityRepository.countRecordsBeforeTime(beforeTime.atStartOfDay())
        electricityRepository.deleteDataBeforeTime(beforeTime.atStartOfDay())
        log.info("Deleted $recordsToDelete electricity records")
    }
}