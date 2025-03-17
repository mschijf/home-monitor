package ms.homemonitor.system.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SystemScheduler(
    private val systemService: SystemService
) {

    @Scheduled(cron = "\${home-monitor.scheduler.system.dbStats}")
    fun dbStats() {
        systemService.processDbStats()
    }

    @Scheduled(cron = "\${home-monitor.scheduler.system.backup}")
    fun doBackup() {
        systemService.executeBackup()
        systemService.cleanUp()
    }

    @Scheduled(cron = "\${home-monitor.scheduler.system.dropboxFreeSpace}")
    fun dropboxFreeSpace() {
        systemService.processBackupStats()
    }

    @Scheduled(cron = "\${home-monitor.scheduler.system.temperature}")
    fun systemTemperatureMeasurement() {
        systemService.processMeasurement()
    }
}