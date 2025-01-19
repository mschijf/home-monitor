package ms.homemonitor.tado.domain.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.tado.domain.rest.TadoClient
import ms.homemonitor.tado.data.model.TadoEntity
import ms.homemonitor.tado.data.repository.TadoRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TadoService(
    private val tado: TadoClient,
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
                    time = now,
                    insideTemperature = tadoResponse.tadoState.sensorDataPoints.insideTemperature.celsius,
                    humidityPercentage = tadoResponse.tadoState.sensorDataPoints.humidity.percentage,
                    heatingPowerPercentage = tadoResponse.tadoState.activityDataPoints.heatingPower.percentage,
                    settingPowerOn = tadoResponse.tadoState.setting.power == "ON",
                    settingTemperature = tadoResponse.tadoState.setting.temperature?.celsius ?: 0.0,
                    outsideTemperature = tadoResponse.weather.outsideTemperature.celsius,
                    solarIntensityPercentage = tadoResponse.weather.solarIntensity.percentage,
                    weatherState = tadoResponse.weather.weatherState.value
                )
            )
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tado data", e)
        }
    }

//    fun processTadoHistoricalData() {
//        val startDate = LocalDate.of(2024, 1, 1)
//        val endDate = LocalDate.of(2024, 11, 17)
//        dateRangeByDay(startDate, endDate).forEach { day ->
//            println("processing $day")
//            val entityList = tado.processHistoricalDay(day)
//            tadoRepository.saveAll(entityList)
//            tadoRepository.flush()
//        }
//        println("Done")
//    }
//
}