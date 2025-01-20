package ms.homemonitor.tado.domain.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TadoScheduler(
    private val tadoService: TadoService,
    @Value("\${tado.enabled}") private val enabled: Boolean
) {

    @Scheduled(cron = "0 * * * * *")
    fun tadoMeasurement() {
        if (enabled)
            tadoService.processMeasurement()
    }
}