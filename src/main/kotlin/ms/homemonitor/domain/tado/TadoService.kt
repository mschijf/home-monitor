package ms.homemonitor.domain.tado

import ms.homemonitor.application.HomeMonitorException
import ms.homemonitor.domain.tado.rest.Tado
import ms.homemonitor.repository.tado.TadoEntity
import ms.homemonitor.repository.tado.TadoRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TadoService(
    private val tado: Tado,
    private val tadoRepository: TadoRepository,
    @Value("\${tado.enabled}") private val enabled: Boolean) {


    @Scheduled(cron = "0 * * * * *")
    fun tadoMeasurement() {
        if (!enabled)
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
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tado data", e)
        }
    }
}