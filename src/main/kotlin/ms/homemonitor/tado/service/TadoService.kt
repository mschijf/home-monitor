package ms.homemonitor.tado.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.tado.repository.TadoHourAggregateRepository
import ms.homemonitor.tado.repository.TadoRepository
import ms.homemonitor.tado.repository.model.TadoEntity
import ms.homemonitor.tado.repository.model.TadoHourAggregateEntity
import ms.homemonitor.tado.restclient.TadoClient
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class TadoService(
    private val tadoClient: TadoClient,
    private val tadoRepository: TadoRepository,
    private val tadoHistoricalDataProcessor: TadoHistoricalDataProcessor,
    private val tadoHourAggregateRepository: TadoHourAggregateRepository,
) {

    fun processMeasurement(timeUnit: TimeUnit) {
        try {
            val now = LocalDateTime.now()
            val tadoResponse = tadoClient.getTadoResponse()
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
                    weatherState = tadoResponse.weather.weatherState.value,
                    callForHeat = callForHeatValue(tadoResponse.tadoState.activityDataPoints.heatingPower.percentage),
                    density = if (timeUnit == TimeUnit.MINUTES) "m" else "h"
                )
            )
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tado data", e)
        }
    }

    fun processHourAggregateMeasurement(day:LocalDate = LocalDate.now()) {
        tadoHistoricalDataProcessor.getHourAggregateList(day, useFile = false).forEach { tadoHour ->
            try {
                tadoHourAggregateRepository.saveAndFlush(
                    TadoHourAggregateEntity(
                        time = tadoHour.time,
                        insideTemperature = tadoHour.insideTemperature,
                        outsideTemperature = tadoHour.outsideTemperature,
                        humidityPercentage = tadoHour.humidityPercentage,
                        powerOnMinutes = tadoHour.powerOnMinutes,
                        settingTemperature = tadoHour.settingTemperature,
                        sunnyMinutes = tadoHour.sunnyMinutes,
                        weatherState = tadoHour.weatherState,
                        callForHeat = tadoHour.callForHeat,
                    )
                )
            } catch (e: Exception) {
                throw HomeMonitorException("Error while processing Tado Aggregate Data", e)
            }
        }
    }


    private fun callForHeatValue(heatingPowerPercentage: Double): Int {
        return when {
            (heatingPowerPercentage <= 0) -> 0
            (heatingPowerPercentage >= 1) and (heatingPowerPercentage <= 33) -> 10
            (heatingPowerPercentage >= 34) and (heatingPowerPercentage <= 66) -> 20
            (heatingPowerPercentage >= 67) and (heatingPowerPercentage <= 100) -> 30
            else -> 0
        }
    }

//    fun processHistory() {
//        val start = LocalDate.of(2024, 4, 1)
//        val end = LocalDate.now()
//        dateRangeByDay(start, end).forEach { day ->
//            println(day)
//            processHourAggregateMeasurement(day)
//        }
//    }
}