package ms.homemonitor.service

import ms.homemonitor.config.TadoProperties
import ms.homemonitor.infra.tado.model.TadoResponseModel
import ms.homemonitor.infra.tado.rest.Tado
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.TadoRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TadoService(
    private val tado: Tado,
    private val repository: TadoRepository,
    private val measurement: MicroMeterMeasurement,
    private val tadoProperties: TadoProperties,
) {

    @Scheduled(cron = "0 * * * * *")
    fun tadoMeasurement() {
        if (!tadoProperties.enabled)
            return
        val tadoResponse = tado.getTadoResponse()
        repository.storeTadoData(tadoResponse)
        setMetrics(tadoResponse)
    }

    fun setMetrics(data: TadoResponseModel) {
        measurement.setDoubleGauge("tadoInsideTemperature", data.tadoState.sensorDataPoints.insideTemperature.celsius)
        measurement.setDoubleGauge("tadoHumidityPercentage", data.tadoState.sensorDataPoints.humidity.percentage)
        measurement.setDoubleGauge("tadoHeatingPowerPercentage", data.tadoState.activityDataPoints.heatingPower.percentage)

        measurement.setDoubleGauge("tadoOutsideTemperature", data.weather.outsideTemperature.celsius)
        measurement.setDoubleGauge("tadoSolarPercentage", data.weather.solarIntensity.percentage)
    }
}