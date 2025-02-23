package ms.homemonitor.tado.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TadoScheduler(
    private val tadoService: TadoService,
    @Value("\${home-monitor.tado.enabled}") private val enabled: Boolean
) {

    @Scheduled(cron = "0 * * * * *")
    fun tadoMeasurement() {
        if (enabled)
            tadoService.processMeasurement(TimeUnit.MINUTES)
    }
}