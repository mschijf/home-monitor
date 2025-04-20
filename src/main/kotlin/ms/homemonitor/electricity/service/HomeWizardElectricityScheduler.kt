package ms.homemonitor.electricity.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HomeWizardElectricityScheduler(
    private val homeWizardElectricityService: HomeWizardElectricityService
) {

    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.electricity.detailed}")
    fun detailedElectricityMeasurement() {
        try {
            homeWizardElectricityService.processMeasurement(persistentStore = false)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    @Scheduled(cron = "\${home-monitor.scheduler.electricity.regular}")
    fun minuteMeasurement() {
        try {
            homeWizardElectricityService.processMeasurement(persistentStore = true)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}