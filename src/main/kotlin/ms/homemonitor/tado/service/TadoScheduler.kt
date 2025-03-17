package ms.homemonitor.tado.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TadoScheduler(
    private val tadoService: TadoService
) {

    @Scheduled(cron = "\${home-monitor.scheduler.tado.regular}")
    fun tadoMeasurement() {
        tadoService.processMeasurement(TimeUnit.MINUTES)
    }
}