package ms.homemonitor.system.service

import ms.homemonitor.system.cliclient.SystemClient
import ms.homemonitor.system.cliclient.model.SystemTemperatureModel
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service

@Service
class SystemTemperatureService(
    private val systemClient: SystemClient,
    private val measurement: MicroMeterMeasurement,
    ) {

    fun processMeasurement() {
        val stats = systemClient.getSystemTemperature()
        setMetrics(stats)
    }

    private fun setMetrics(data: SystemTemperatureModel) {
        measurement.setDoubleGauge("systemCpuTemperature", data.cpuTemperature)
        measurement.setDoubleGauge("systemGpuTemperature", data.gpuTemperature)
    }
}