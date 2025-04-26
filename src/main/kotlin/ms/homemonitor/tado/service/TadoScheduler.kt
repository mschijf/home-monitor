package ms.homemonitor.tado.service

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TadoScheduler(
    private val tadoService: TadoService,
    private val tadoDayReportService: TadoDayReportService
) {

    val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${home-monitor.scheduler.tado.regular}")
    fun tadoMeasurement() {
        try {
            tadoService.processMeasurement()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    @Scheduled(cron = "\${home-monitor.scheduler.tado.hourSummary}")
    fun tadoMeasurementHour() {
        try {
            tadoDayReportService.processHourAggregateMeasurement()
        } catch (e: Exception) {
            log.error(e.message, e)
        }

    }

    @Scheduled(cron = "\${home-monitor.scheduler.tado.cleanup}")
    fun tadoCleanup() {
        try {
            tadoService.cleanupOldData(keepDays = 90L)
        } catch (e: Exception) {
            log.error(e.message, e)
        }
    }
}