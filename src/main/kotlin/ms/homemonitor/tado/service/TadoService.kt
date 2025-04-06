package ms.homemonitor.tado.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.tado.repository.TadoHourAggregateRepository
import ms.homemonitor.tado.repository.TadoRepository
import ms.homemonitor.tado.repository.model.TadoEntity
import ms.homemonitor.tado.repository.model.TadoHourAggregateEntity
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.tado.service.model.TadoDayReportTimeUnit
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TadoService(
    private val tadoClient: TadoClient,
    private val tadoRepository: TadoRepository,
    private val tadoHourAggregateRepository: TadoHourAggregateRepository,
) {

    fun processMeasurement() {
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
                )
            )
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tado data", e)
        }
    }

    fun processHourAggregateMeasurement(day:LocalDate = LocalDate.now()) {
        getHourAggregateList(day).forEach { tadoHour ->
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

    private fun getHourAggregateList(day: LocalDate): List<TadoDayReportTimeUnit> {
        val tadoDayReport = tadoClient.getTadoHistoricalInfo(day)
        return TadoDayReportDetails(tadoDayReport, day).getHourList()
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