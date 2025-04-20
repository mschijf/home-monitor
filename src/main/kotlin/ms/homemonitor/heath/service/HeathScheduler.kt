package ms.homemonitor.heath.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HeathScheduler(
    private val heathService: HeathService
) {
    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.heath.updateEnecoStats}")
    fun updateEnecoStatistics() {
        try {
            heathService.processMeaurement()
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}