package ms.homemonitor.dbstats.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DbStatsScheduler(
    private val dbStatsService: DbStatsService,
    @Value("\${home-monitor.dbstats.enabled}") private val enabled: Boolean,
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

}