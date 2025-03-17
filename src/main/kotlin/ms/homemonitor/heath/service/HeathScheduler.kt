package ms.homemonitor.heath.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HeathScheduler(
    private val heathService: HeathService
) {

    @Scheduled(cron = "\${home-monitor.scheduler.heath.updateEnecoStats}")
    fun updateEnecoStatistics() {
        heathService.processMeaurement()
    }
}