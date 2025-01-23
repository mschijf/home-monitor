package ms.homemonitor.tado.domain.service

import ms.homemonitor.tado.domain.model.TadoReportTimeUnit
import ms.homemonitor.tado.domain.model.callForHeatToInt
import ms.homemonitor.tado.restclient.model.TadoDayReport
import java.time.LocalDateTime

data class TadoDayReportDetails(val tadoDayReport: TadoDayReport) {

    private val insideTemperaturelist = tadoDayReport.measuredData.insideTemperature.dataPoints.map { it.timestamp to it.value.celsius }.sortedBy { it.first }
    private val humidityPercentageList = tadoDayReport.measuredData.humidity.dataPoints.map { it.timestamp to it.value }.sortedBy { it.first }
    private val settingPowerOnList = tadoDayReport.settings.dataIntervals.map { it.from to it.value.power }.sortedBy { it.first }
    private val settingTemperatureList = tadoDayReport.settings.dataIntervals.map { it.from to it.value.temperature }.sortedBy { it.first }

    private val outsideTemperatureList = tadoDayReport.weather.condition.dataIntervals.map { it.from to it.value.temperature }.sortedBy { it.first }
    private val weatherStateList = tadoDayReport.weather.condition.dataIntervals.map { it.from to it.value.state }.sortedBy { it.first }

    private val callForHeatList = tadoDayReport.callForHeat.dataIntervals.map { it.from to it.value }.sortedBy { it.first }
    private val isSunnyList = tadoDayReport.weather.sunny.dataIntervals.map { it.from to it.value }.sortedBy { it.first }

    fun getTadoReportTimeUnit(localTime: LocalDateTime): TadoReportTimeUnit {
        val time = localTime.minusHours(1)
        val insideTemperature = insideTemperaturelist.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second
        val humidityPercentage = humidityPercentageList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second
        val callForHeat = callForHeatList.lastOrNull{ it.first.isBefore(time.plusNanos(1)) }?.second
        val settingPowerOn = (settingPowerOnList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second?:"OFF") == "ON"
        val settingTemperature = settingTemperatureList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second?.celsius?:0.0
        val outsideTemperature = outsideTemperatureList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second?.celsius!!
        val isSunny = isSunnyList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second == true
        val weatherState = weatherStateList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second

        return TadoReportTimeUnit(
            localTime,
            insideTemperature, humidityPercentage?.times(100.0),
            settingPowerOn, callForHeatToInt(callForHeat), settingTemperature,
            outsideTemperature, isSunny, weatherState,
        )
    }
}