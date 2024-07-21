package ms.powermonitoring.service

import ms.powermonitoring.config.ApplicationOutputProperties
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class Scheduler(
    private val service: PowerMonitoringService,
    private val applicationOutputProperties: ApplicationOutputProperties) {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        log.info("Output to file is ${if (applicationOutputProperties.enabled) "enabled with ${applicationOutputProperties.variableTimeFetchRateInMilliseconds} ms as variable-fetch-rate" else "disabled"} ")
    }

    //@Scheduled(fixedRate = applicationOutputProperties.variableTimeFetchRateInMilliseconds.toLong())
    @Scheduled(fixedRate = 10000)
    fun runMostDetailed() {
        if (applicationOutputProperties.enabled) {
            service.variableTimedPowerMeasurement()
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    fun runHour() {
        if (applicationOutputProperties.enabled) {
            service.storeHourPowerMeasurement()
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun runDay() {
        if (applicationOutputProperties.enabled) {
            service.storeDayPowerMeasurement()
        }
    }

}