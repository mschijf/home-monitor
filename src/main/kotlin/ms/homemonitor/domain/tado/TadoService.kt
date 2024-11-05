package ms.homemonitor.domain.tado

import ms.homemonitor.application.HomeMonitorException
import ms.homemonitor.domain.tado.model.TadoResponseModel
import ms.homemonitor.domain.tado.rest.Tado
import ms.homemonitor.micrometer.MicroMeterMeasurement
import ms.homemonitor.repository.TadoEntity
import ms.homemonitor.repository.TadoRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TadoService(
    private val tado: Tado,
    private val tadoRepository: TadoRepository,
    private val measurement: MicroMeterMeasurement,
    private val tadoProperties: TadoProperties,
) {

    @Scheduled(cron = "0 * * * * *")
    fun tadoMeasurement() {
        if (!tadoProperties.enabled)
            return
        try {
            val now = LocalDateTime.now()
            val tadoResponse = tado.getTadoResponse()
            tadoRepository.saveAndFlush(
                TadoEntity(
                    time= now,
                    insideTemperature = tadoResponse.tadoState.sensorDataPoints.insideTemperature.celsius,
                    humidityPercentage = tadoResponse.tadoState.sensorDataPoints.humidity.percentage,
                    heatingPowerPercentage = tadoResponse.tadoState.activityDataPoints.heatingPower.percentage,
                    settingPowerOn = tadoResponse.tadoState.setting.power == "ON",
                    settingTemperature = tadoResponse.tadoState.setting.temperature?.celsius?:0.0,
                    outsideTemperature = tadoResponse.weather.outsideTemperature.celsius,
                    solarIntensityPercentage = tadoResponse.weather.solarIntensity.percentage,
                    weatherState = tadoResponse.weather.weatherState.value
                )
            )
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