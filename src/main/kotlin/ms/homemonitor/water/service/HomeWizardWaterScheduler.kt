package ms.homemonitor.water.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HomeWizardWaterScheduler(
    private val homeWizardWaterService: HomeWizardWaterService
) {

    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.water.detailed}")
    fun detailedWaterMeasurement() {
        try {
            homeWizardWaterService.processMeasurement(persistentStore = false)
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    @Scheduled(cron = "\${home-monitor.scheduler.water.regular}")
    fun minuteMeasurement() {
        try {
            homeWizardWaterService.processMeasurement(persistentStore = true)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    @Scheduled(cron = "\${home-monitor.scheduler.water.cleanup}")
    fun waterCleanup() {
        try {
            homeWizardWaterService.cleanupOldData(keepDays = 90L)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

}