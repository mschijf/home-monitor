package ms.homemonitor.eneco.domain.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class EnecoScheduler(
    private val enecoService: EnecoService,
    @Value("\${eneco.enabled}") private val enabled: Boolean,
) {

    @Scheduled(cron = "0 0 0/2 * * *")
    fun updateEnecoStatistics() {
        if (enabled)
            enecoService.processMeaurement()
    }
}