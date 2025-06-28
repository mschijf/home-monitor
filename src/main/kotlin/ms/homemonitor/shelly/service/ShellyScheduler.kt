package ms.homemonitor.shelly.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ShellyScheduler(
    private val shellyService: ShellyService,
) {

    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.shelly.regular}")
    fun tadoMeasurement() {
        try {
            shellyService.processMeasurement()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

}