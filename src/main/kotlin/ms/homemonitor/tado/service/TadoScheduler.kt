package ms.homemonitor.tado.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TadoScheduler(
    private val tadoService: TadoService,
    private val tadoDayReportService: TadoDayReportService
) {

    @Scheduled(cron = "\${home-monitor.scheduler.tado.regular}")
    fun tadoMeasurement() {
        tadoService.processMeasurement()
    }

    @Scheduled(cron = "\${home-monitor.scheduler.tado.hourSummary}")
    fun tadoMeasurementHour() {
        tadoDayReportService.processHourAggregateMeasurement()
    }

}