package ms.homemonitor.smartplug.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SmartPlugScheduler(
    private val smartPlugService: SmartPlugService
) {
    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.smartPlug.regular}")
    fun retrieveSmartPlugData() {
        try {
            smartPlugService.processMeasurement()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

    @Scheduled(cron = "\${home-monitor.scheduler.smartPlug.deviceState}")
    fun retrieveSmartPlugDataStatus() {
        try {
            smartPlugService.processSmartPlugStatus()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }

}