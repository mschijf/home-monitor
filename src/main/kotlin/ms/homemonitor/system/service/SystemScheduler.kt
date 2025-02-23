package ms.homemonitor.system.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SystemScheduler(
    private val dbStatsService: DbStatsService,
    private val backupService: BackupService,
    private val systemTemperatureService: SystemTemperatureService,
    @Value("\${home-monitor.system.enabled}") private val enabled: Boolean,
    @Value("\${home-monitor.system.backup.enabled}") private val backupEnabled: Boolean,
) {

    @Scheduled(cron = "0 * * * * *")
    fun dbStats() {
        if (enabled)
            dbStatsService.processDbStats()
    }

    @Scheduled(cron = "0 0 * * * *")
    fun doBackup() {
        if (backupEnabled)
            backupService.executeBackup()
    }

    @Scheduled(cron = "0 10 * * * *")
    fun dropboxFreeSpace() {
        if (enabled)
            dbStatsService.processBackupStats()
    }

    @Scheduled(cron = "0 * * * * *")
    fun systemTemperatureMeasurement() {
        if (enabled)
            systemTemperatureService.processMeasurement()
    }
}