package ms.homemonitor.raspberrypi.domain.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RaspberryPiScheduler(
    private val raspberryPiService: RaspberryPiService,
    @Value("\${raspberrypi.enabled}") private val enabled: Boolean
) {

    @Scheduled(cron = "0 * * * * *")
    fun raspberryPiMeasurement() {
        if (enabled)
            raspberryPiService.processMeasurement()
    }
}