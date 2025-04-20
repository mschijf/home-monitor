package ms.homemonitor.system.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SystemScheduler(
    private val systemService: SystemService
) {

    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.system.dbStats}")
    fun dbStats() {
        try {
            systemService.processDbStats()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    @Scheduled(cron = "\${home-monitor.scheduler.system.backup}")
    fun doBackup() {
        try {
            systemService.executeBackup()
            systemService.cleanUp()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    @Scheduled(cron = "\${home-monitor.scheduler.system.dropboxFreeSpace}")
    fun dropboxFreeSpace() {
        try {
            systemService.processBackupStats()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    @Scheduled(cron = "\${home-monitor.scheduler.system.temperature}")
    fun systemTemperatureMeasurement() {
        try {
            systemService.processMeasurement()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }
}