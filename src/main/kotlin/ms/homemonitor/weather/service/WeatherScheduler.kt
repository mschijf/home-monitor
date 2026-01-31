package ms.homemonitor.weather.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class WeatherScheduler(
    private val weatherService: WeatherService,
) {

    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.weather.regular}")
    fun weatherMeasurement() {
        try {
            weatherService.processMeasurement()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

}