package ms.homemonitor.system.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SystemScheduler(
    private val systemService: SystemService,
    @Value("\${home-monitor.system.enabled}") private val enabled: Boolean,
    @Value("\${home-monitor.system.backup.enabled}") private val backupEnabled: Boolean,
) {

    @Scheduled(cron = "0 * * * * *")
    fun dbStats() {
        if (enabled)
            systemService.processDbStats()
    }

    @Scheduled(cron = "0 0 * * * *")
    fun doBackup() {
        if (backupEnabled) {
            systemService.executeBackup()
            systemService.cleanUp()
        }
    }

    @Scheduled(cron = "0 10 * * * *")
    fun dropboxFreeSpace() {
        if (enabled)
            systemService.processBackupStats()
    }

    @Scheduled(cron = "0 * * * * *")
    fun systemTemperatureMeasurement() {
        if (enabled)
            systemService.processMeasurement()
    }
}