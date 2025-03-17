package ms.homemonitor.water.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HomeWizardWaterScheduler(
    private val homeWizardWaterService: HomeWizardWaterService
) {

    @Scheduled(cron = "\${home-monitor.scheduler.water.detailed}")
    fun detailedWaterMeasurement() {
        homeWizardWaterService.processMeasurement(persistentStore = false)
    }

    @Scheduled(cron = "\${home-monitor.scheduler.water.regular}")
    fun minuteMeasurement() {
        homeWizardWaterService.processMeasurement(persistentStore = true)
    }

}