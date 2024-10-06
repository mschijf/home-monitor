package ms.homemonitor.domain.tado

import ms.homemonitor.application.HomeMonitorException
import ms.homemonitor.domain.tado.model.TadoResponseModel
import ms.homemonitor.domain.tado.repository.TadoRepository
import ms.homemonitor.domain.tado.rest.Tado
import ms.homemonitor.micrometer.MicroMeterMeasurement
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
        try {
            val tadoResponse = tado.getTadoResponse()
            repository.storeTadoData(tadoResponse)
            setMetrics(tadoResponse)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tado data", e)
        }

    }

    fun setMetrics(data: TadoResponseModel) {
        measurement.setDoubleGauge("tadoInsideTemperature", data.tadoState.sensorDataPoints.insideTemperature.celsius)
        measurement.setDoubleGauge("tadoHumidityPercentage", data.tadoState.sensorDataPoints.humidity.percentage)
        measurement.setDoubleGauge("tadoHeatingPowerPercentage", data.tadoState.activityDataPoints.heatingPower.percentage)

        measurement.setDoubleGauge("tadoOutsideTemperature", data.weather.outsideTemperature.celsius)
        measurement.setDoubleGauge("tadoSolarPercentage", data.weather.solarIntensity.percentage)
    }
}