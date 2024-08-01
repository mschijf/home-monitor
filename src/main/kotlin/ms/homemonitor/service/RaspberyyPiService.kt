package ms.homemonitor.service

import ms.homemonitor.config.RaspberryPiProperties
import ms.homemonitor.infra.raspberrypi.RaspberryPiStats
import ms.homemonitor.infra.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.monitor.MicroMeterMeasurement
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RaspberyyPiService(
    private val raspberryPiStats: RaspberryPiStats,
    private val measurement: MicroMeterMeasurement,
    private val raspberryPiProperties: RaspberryPiProperties
) {

    @Scheduled(cron = "0 * * * * *")
    fun raspberryPiMeasurement() {
        if (!raspberryPiProperties.enabled)
            return
        val stats = raspberryPiStats.getRaspberryPiStats()
        setMetrics(stats)
    }

    fun setMetrics(data: RaspberryPiStatsModel) {
        measurement.setDoubleGauge("systemCpuTemperature", data.cpuTemperature)
        measurement.setDoubleGauge("systemGpuTemperature", data.gpuTemperature)
    }
}