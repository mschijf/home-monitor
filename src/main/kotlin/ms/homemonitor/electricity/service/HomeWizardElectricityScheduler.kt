package ms.homemonitor.electricity.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HomeWizardElectricityScheduler(
    private val homeWizardElectricityService: HomeWizardElectricityService
) {

    @Scheduled(cron = "\${home-monitor.scheduler.electricity.detailed}")
    fun detailedElectricityMeasurement() {
        homeWizardElectricityService.processMeasurement(persistentStore = false)
    }

    @Scheduled(cron = "\${home-monitor.scheduler.electricity.regular}")
    fun minuteMeasurement() {
        homeWizardElectricityService.processMeasurement(persistentStore = true)
    }
}