package ms.powermonitoring.service

import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

const val delay = 10000L //env.getProperty("output.most-detailed.fetch-rate-in-milliseconds")?.toLong() ?: 1000L

@Service
class Scheduler(
    private val service: PowerMonitoringService,
    environment: Environment) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val enabledMostDetailed = environment.getProperty("output.most-detailed.enabled")?.toBoolean() ?: false
    private val enabledHour = environment.getProperty("output.hour.enabled")?.toBoolean() ?: false
    private val enabledDay = environment.getProperty("output.day.enabled")?.toBoolean() ?: false

    init {
        log.info("Most detailed level measuring ${if (enabledMostDetailed) "enabled for " + delay + " ms" else "disabled"} ")
        log.info("Hour level measuring ${if (enabledHour) "enabled" else "disabled"}")
        log.info("Day level measuring ${if (enabledDay) "enabled" else "disabled"}")
    }

    @Scheduled(fixedRate = delay)
    fun runMostDetailed() {
        if (enabledMostDetailed) {
            service.storeMostDetailedPowerMeasurement()
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    fun runHour() {
        if (enabledHour) {
            service.storeHourPowerMeasurement()
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun runDay() {
        if (enabledDay) {
            service.storeDayPowerMeasurement()
        }
    }

}