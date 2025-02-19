package ms.homemonitor.tado.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.tado.repository.TadoRepository
import ms.homemonitor.tado.repository.model.TadoEntity
import ms.homemonitor.tado.restclient.TadoClient
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class TadoService(
    private val tadoClient: TadoClient,
    private val tadoRepository: TadoRepository) {

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

    private fun callForHeatValue(heatingPowerPercentage: Double): Int {
        return when {
            (heatingPowerPercentage <= 0) -> 0
            (heatingPowerPercentage >= 1) and (heatingPowerPercentage <= 33) -> 10
            (heatingPowerPercentage >= 34) and (heatingPowerPercentage <= 66) -> 20
            (heatingPowerPercentage >= 67) and (heatingPowerPercentage <= 100) -> 30
            else -> 0
        }
    }

//    fun processDayReport(day: LocalDate = LocalDate.now()) {
//        val today = day
//        val yesterday = today.minusDays(1)
//        val dayReport = tadoClient.getTadoHistoricalInfo(yesterday)
//        val measuredList = tadoRepository.findBetweenDates(yesterday.atStartOfDay(), today.atStartOfDay())
//        val tadoDayDetails = TadoDayReportDetails(dayReport)
//        measuredList.forEach { currentEntity ->
//            val dataReportEntity = tadoDayDetails.getTadoReportTimeUnit(currentEntity.time)
//            if (currentEntity.callForHeat == null) {
//                currentEntity.callForHeat = dataReportEntity.callForHeat
//                tadoRepository.save(currentEntity)
//            } else {
//                if (currentEntity.callForHeat != dataReportEntity.callForHeat) {
//                    log.warn("${currentEntity.time} 'call for heat' has value ${currentEntity.callForHeat}, but ${dataReportEntity.callForHeat} was expected")
//                    currentEntity.callForHeat = dataReportEntity.callForHeat
//                    tadoRepository.save(currentEntity)
//                }
//            }
//        }
//        tadoRepository.flush()
//    }
//
//    fun processHistory() {
//        val start = LocalDate.of(2024, 3, 30)
//        val end = LocalDate.of(2024, 11, 17)
//        dateRangeByDay(start, end).forEach { day ->
//            println(day)
//            val list = tadoHistoricalDataProcessor
//                .processHistoricalDay(day, useFile = true)
//                .map { dayUnit -> TadoEntity(
//                    time = dayUnit.time,
//                    insideTemperature = dayUnit.insideTemperature,
//                    humidityPercentage = dayUnit.humidityPercentage,
//                    heatingPowerPercentage = null,
//                    settingPowerOn = dayUnit.settingPowerOn,
//                    settingTemperature = dayUnit.settingTemperature,
//                    outsideTemperature = dayUnit.outsideTemperature,
//                    solarIntensityPercentage = null,
//                    weatherState = dayUnit.weatherState,
//                    callForHeat = dayUnit.callForHeat,
//                    density = "h"
//                ) }
//            list.forEach { tadoRepository.saveAndFlush(it) }
//        }
//    }
}