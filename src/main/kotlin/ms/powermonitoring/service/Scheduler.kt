package ms.powermonitoring.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

const val delay = 10000L //env.getProperty("low-level.scheduler.frequency-in-milliseconds")?.toLong() ?: 1000L

@Service
class Scheduler(
    private val service: PowerMonitoringService) {

    @Scheduled(fixedRate = delay)
    fun run() {
        service.storeLatestPowerMeasurement()
    }
}