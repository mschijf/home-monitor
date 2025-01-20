package ms.homemonitor.electricity.domain.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HomeWizardElectricityScheduler(
    private val homeWizardElectricityService: HomeWizardElectricityService,
    @Value("\${homewizard.enabled}") private val enabled: Boolean,
) {

    @Scheduled(fixedRate = 10_000)
    fun detailedElectricityMeasurement() {
        if (enabled)
            homeWizardElectricityService.processMeasurement(persistentStore = false)
    }

    @Scheduled(cron = "0 * * * * *")
    fun minuteMeasurement() {
        if (enabled)
            homeWizardElectricityService.processMeasurement(persistentStore = true)
    }
}