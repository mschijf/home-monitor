package ms.homemonitor.system.service

import ms.homemonitor.system.cliclient.RaspberryPiStats
import ms.homemonitor.system.cliclient.model.RaspberryPiStatsModel
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service

@Service
class RaspberryPiService(
    private val raspberryPiStats: RaspberryPiStats,
    private val measurement: MicroMeterMeasurement,
    ) {

    fun processMeasurement() {
        val stats = raspberryPiStats.getRaspberryPiStats()
        setMetrics(stats)
    }

    private fun setMetrics(data: RaspberryPiStatsModel) {
        measurement.setDoubleGauge("systemCpuTemperature", data.cpuTemperature)
        measurement.setDoubleGauge("systemGpuTemperature", data.gpuTemperature)
    }
}