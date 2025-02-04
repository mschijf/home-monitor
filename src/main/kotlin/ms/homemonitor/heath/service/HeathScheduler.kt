package ms.homemonitor.heath.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class HeathScheduler(
    private val heathService: HeathService,
    @Value("\${eneco.enabled}") private val enabled: Boolean,
) {

    @Scheduled(cron = "0 0 1-23/2 * * *")
    fun updateEnecoStatistics() {
        if (enabled)
            heathService.processMeaurement()
    }
}