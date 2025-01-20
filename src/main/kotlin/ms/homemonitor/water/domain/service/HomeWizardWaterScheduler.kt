package ms.homemonitor.water.domain.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HomeWizardWaterScheduler(
    private val homeWizardWaterService: HomeWizardWaterService,
    @Value("\${homewizard.enabled}") private val enabled: Boolean,
) {

    @Scheduled(fixedRate = 10_000)
    fun detailedWaterMeasurement() {
        if (enabled)
            homeWizardWaterService.processMeasurement(persistentStore = false)
    }

    @Scheduled(cron = "0 * * * * *")
    fun minuteMeasurement() {
        if (enabled)
            homeWizardWaterService.processMeasurement(persistentStore = true)
    }

}