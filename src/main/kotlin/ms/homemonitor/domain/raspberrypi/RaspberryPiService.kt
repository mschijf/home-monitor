package ms.homemonitor.domain.raspberrypi

import ms.homemonitor.domain.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.domain.raspberrypi.rest.RaspberryPiStats
import ms.homemonitor.micrometer.MicroMeterMeasurement
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RaspberryPiService(
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