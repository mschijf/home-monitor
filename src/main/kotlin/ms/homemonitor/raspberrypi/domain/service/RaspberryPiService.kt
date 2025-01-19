package ms.homemonitor.raspberrypi.domain.service

import ms.homemonitor.raspberrypi.domain.model.RaspberryPiStatsModel
import ms.homemonitor.raspberrypi.cliclient.RaspberryPiStats
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RaspberryPiService(
    private val raspberryPiStats: RaspberryPiStats,
    private val measurement: MicroMeterMeasurement,
    @Value("\${raspberrypi.enabled}") private val enabled: Boolean,
    ) {

    @Scheduled(cron = "0 * * * * *")
    fun raspberryPiMeasurement() {
        if (!enabled)
            return
        val stats = raspberryPiStats.getRaspberryPiStats()
        setMetrics(stats)
    }

    private fun setMetrics(data: RaspberryPiStatsModel) {
        measurement.setDoubleGauge("systemCpuTemperature", data.cpuTemperature)
        measurement.setDoubleGauge("systemGpuTemperature", data.gpuTemperature)
    }
}