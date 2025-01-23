package ms.homemonitor.tado.domain.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.dateRangeByDay
import ms.homemonitor.tado.data.model.TadoEntity
import ms.homemonitor.tado.data.repository.TadoRepository
import ms.homemonitor.tado.restclient.TadoClient
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TadoService(
    private val tadoClient: TadoClient,
    private val tadoRepository: TadoRepository,
    private val tadoHistoricalDataProcessor: TadoHistoricalDataProcessor) {

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
                    callForHeat = null
                )
            )
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tado data", e)
        }
    }

    fun processDayReport() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val dayReport = tadoClient.getTadoHistoricalInfo(yesterday)
        val measuredList = tadoRepository.findBetweenDates(yesterday.atStartOfDay(), today.atStartOfDay())
        val tadoDayDetails = TadoDayReportDetails(dayReport)
        measuredList.forEach { currentEntity ->
            val dataReportEntity = tadoDayDetails.getTadoReportTimeUnit(currentEntity.time)
            currentEntity.callForHeat = dataReportEntity.callForHeat
            tadoRepository.save(currentEntity)
        }
        tadoRepository.flush()
    }

    fun processHistory() {
        val start = LocalDate.of(2024, 3, 30)
        val end = LocalDate.of(2024, 11, 17)
        dateRangeByDay(start, end).forEach { day ->
            println(day)
            val list = tadoHistoricalDataProcessor
                .processHistoricalDay(day, useFile = true)
                .map { dayUnit -> TadoEntity(
                    time = dayUnit.time,
                    insideTemperature = dayUnit.insideTemperature,
                    humidityPercentage = dayUnit.humidityPercentage,
                    heatingPowerPercentage = null,
                    settingPowerOn = dayUnit.settingPowerOn,
                    settingTemperature = dayUnit.settingTemperature,
                    outsideTemperature = dayUnit.outsideTemperature,
                    solarIntensityPercentage = null,
                    weatherState = dayUnit.weatherState,
                    callForHeat = dayUnit.callForHeat
                ) }
            list.forEach { tadoRepository.saveAndFlush(it) }
        }
    }
}