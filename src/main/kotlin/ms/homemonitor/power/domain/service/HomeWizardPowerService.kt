package ms.homemonitor.power.domain.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.domain.service.SummaryService
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.power.data.model.PowerEntity
import ms.homemonitor.power.data.repository.PowerRepository
import ms.homemonitor.power.domain.model.HomeWizardEnergyData
import ms.homemonitor.power.domain.rest.HomeWizard
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class HomeWizardPowerService(
    private val homeWizard: HomeWizard,
    private val measurement: MicroMeterMeasurement,
    private val powerRepository: PowerRepository,
    private val summary: SummaryService,
    @Value("\${homewizard.enabled}") private val enabled: Boolean,
) {

    @Scheduled(fixedRate = 10_000)
    fun detailedPowerMeasurement() {
        if (!enabled)
            return

        try {
            val homeWizardEnergyData = homeWizard.getHomeWizardEnergyData()
            setMetrics(homeWizardEnergyData)
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
            val homeWizardEnergyData = homeWizard.getHomeWizardEnergyData()
            powerRepository.saveAndFlush(
                PowerEntity(
                    time = now,
                    powerNormalKwh = homeWizardEnergyData.totalPowerImportT2Kwh,
                    powerOffpeakKwh = homeWizardEnergyData.totalPowerImportT1Kwh
                )
            )

        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing and storing HomeWizard data", e)
        }
    }

    fun getPowerYearSummary(): YearSummary {
        return summary.getSummary(powerRepository)
    }

    private fun setMetrics(data: HomeWizardEnergyData) {
        measurement.setDoubleGauge("homewizardActivePowerL1Watt", data.activePowerL1Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL2Watt", data.activePowerL2Watt.toDouble())
        measurement.setDoubleGauge("homewizardActivePowerL3Watt", data.activePowerL3Watt.toDouble())
    }
}