package ms.homemonitor.system.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SystemScheduler(
    private val dbStatsService: DbStatsService,
    private val raspberryPiService: RaspberryPiService,
    @Value("\${home-monitor.system.enabled}") private val enabled: Boolean,
) {

    @Scheduled(cron = "0 * * * * *")
    fun dbStats() {
        if (enabled)
            dbStatsService.processDbStats()
    }

    @Scheduled(cron = "0 10 * * * *")
    fun dropboxFreeSpace() {
        if (enabled)
            dbStatsService.processBackupStats()
    }

    @Scheduled(cron = "0 * * * * *")
    fun raspberryPiMeasurement() {
        if (enabled)
            raspberryPiService.processMeasurement()
    }
}