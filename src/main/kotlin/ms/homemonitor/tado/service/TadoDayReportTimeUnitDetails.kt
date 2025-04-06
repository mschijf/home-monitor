package ms.homemonitor.tado.service

import ms.homemonitor.tado.restclient.model.TadoDayReport
import ms.homemonitor.tado.restclient.model.TadoTemperature
import ms.homemonitor.tado.service.model.TadoDayReportTimeUnit
import java.time.LocalDateTime
import java.time.ZoneId

data class TadoDayReportTimeUnitDetails(
    val insideTemperaturelist: List<Pair<LocalDateTime, Double>>,
    val humidityPercentageList: List<Pair<LocalDateTime, Double>>,
    val settingPowerOnList: List<Pair<LocalDateTime, String>>,
    val settingTemperatureList: List<Pair<LocalDateTime, TadoTemperature?>>,

    val outsideTemperatureList: List<Pair<LocalDateTime, TadoTemperature?>>,
    val weatherStateList: List<Pair<LocalDateTime, String>>,

    val callForHeatList: List<Pair<LocalDateTime, String>>,
    val isSunnyList: List<Pair<LocalDateTime, Boolean>>
) {

    companion object {
        fun of(tadoDayReport: TadoDayReport): TadoDayReportTimeUnitDetails {
            return TadoDayReportTimeUnitDetails(
                insideTemperaturelist = tadoDayReport.measuredData.insideTemperature.dataPoints.map { it.timestamp.tadoTimeToLocalTime() to it.value.celsius }.sortedBy { it.first },
                humidityPercentageList = tadoDayReport.measuredData.humidity.dataPoints.map { it.timestamp.tadoTimeToLocalTime() to it.value }.sortedBy { it.first },
                settingPowerOnList = tadoDayReport.settings.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.power }.sortedBy { it.first },
                settingTemperatureList = tadoDayReport.settings.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.temperature }.sortedBy { it.first },

                outsideTemperatureList = tadoDayReport.weather.condition.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.temperature }.sortedBy { it.first },
                weatherStateList = tadoDayReport.weather.condition.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.state }.sortedBy { it.first },

                callForHeatList = tadoDayReport.callForHeat.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value }.sortedBy { it.first },
                isSunnyList = tadoDayReport.weather.sunny.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value }.sortedBy { it.first }
            )
        }

        private fun LocalDateTime.tadoTimeToLocalTime(): LocalDateTime {
            return this
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
                .toLocalDateTime()
        }
    }

    fun getDetailsForTime(localTime: LocalDateTime): TadoDayReportTimeUnit {
        val insideTemperature = insideTemperaturelist.lastOrNull { it.first.isBefore(localTime.plusNanos(1)) }?.second
        val humidityPercentage = humidityPercentageList.lastOrNull { it.first.isBefore(localTime.plusNanos(1)) }?.second
        val callForHeat = callForHeatList.lastOrNull{ it.first.isBefore(localTime.plusNanos(1)) }?.second
        val settingPowerOn = (settingPowerOnList.lastOrNull { it.first.isBefore(localTime.plusNanos(1)) }?.second?:"OFF") == "ON"
        val settingTemperature = settingTemperatureList.lastOrNull { it.first.isBefore(localTime.plusNanos(1)) }?.second?.celsius?:0.0
        val outsideTemperature = outsideTemperatureList.lastOrNull { it.first.isBefore(localTime.plusNanos(1)) }?.second?.celsius!!
        val isSunny = isSunnyList.lastOrNull { it.first.isBefore(localTime.plusNanos(1)) }?.second == true
        val weatherState = weatherStateList.lastOrNull { it.first.isBefore(localTime.plusNanos(1)) }?.second

        return TadoDayReportTimeUnit(
            time = localTime,
            insideTemperature = insideTemperature,
            outsideTemperature = outsideTemperature,
            humidityPercentage = humidityPercentage,
            powerOnMinutes = if (settingPowerOn) 1 else 0,
            settingTemperature = settingTemperature,
            sunnyMinutes = if (isSunny) 1 else 0,
            weatherState = weatherState,
            callForHeat = callForHeatToInt(callForHeat),
        )
    }

    private fun callForHeatToInt(cfh: String?): Int {
        return when (cfh) {
            "HIGH" -> 30
            "MEDIUM" -> 20
            "LOW" -> 10
            "NONE" -> 0
            null -> 0
            else -> throw Exception("$cfh is an unknown call for-heat-value")
        }
    }

}