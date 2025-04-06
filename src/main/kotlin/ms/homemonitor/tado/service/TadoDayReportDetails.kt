package ms.homemonitor.tado.service

import ms.homemonitor.shared.tools.dateTimeRangeByMinute
import ms.homemonitor.tado.service.model.TadoDayReportTimeUnit
import ms.homemonitor.tado.restclient.model.TadoDayReport
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.roundToInt

class TadoDayReportDetails(private val tadoDayReport: TadoDayReport, private val day: LocalDate) {

    private val insideTemperaturelist = tadoDayReport.measuredData.insideTemperature.dataPoints.map { it.timestamp.tadoTimeToLocalTime() to it.value.celsius }.sortedBy { it.first }
    private val humidityPercentageList = tadoDayReport.measuredData.humidity.dataPoints.map { it.timestamp.tadoTimeToLocalTime() to it.value }.sortedBy { it.first }
    private val settingPowerOnList = tadoDayReport.settings.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.power }.sortedBy { it.first }
    private val settingTemperatureList = tadoDayReport.settings.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.temperature }.sortedBy { it.first }

    private val outsideTemperatureList = tadoDayReport.weather.condition.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.temperature }.sortedBy { it.first }
    private val weatherStateList = tadoDayReport.weather.condition.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value.state }.sortedBy { it.first }

    private val callForHeatList = tadoDayReport.callForHeat.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value }.sortedBy { it.first }
    private val isSunnyList = tadoDayReport.weather.sunny.dataIntervals.map { it.from.tadoTimeToLocalTime() to it.value }.sortedBy { it.first }

    private fun getMinuteList(): List<TadoDayReportTimeUnit> {
        return dateTimeRangeByMinute(day.atStartOfDay(), day.plusDays(1).atStartOfDay().minusSeconds(1))
            .map { time -> getTadoReportTimeUnit(time) }
            .toList()
    }

    fun getHourList(): List<TadoDayReportTimeUnit> {
        val tadoMinuteList = getMinuteList()
        return tadoMinuteList
            .groupBy { it.time.hour }
            .mapValues { it -> aggregateTadoReportTimeUnitMinuteListToHour(it.value) }
            .values.toList()
    }

    private fun aggregateTadoReportTimeUnitMinuteListToHour(tadoMinuteList: List<TadoDayReportTimeUnit>): TadoDayReportTimeUnit {
        val lastTado = tadoMinuteList.last()
        val lastTime = lastTado.time
        val aggregate = TadoDayReportTimeUnit(
            time = LocalDateTime.of(lastTime.year, lastTime.month, lastTime.dayOfMonth, lastTime.hour, 0, 0),
            insideTemperature = tadoMinuteList.sumOf { it.insideTemperature ?: 0.0 } / tadoMinuteList.size,
            outsideTemperature = tadoMinuteList.sumOf { it.outsideTemperature ?: 0.0 } / tadoMinuteList.size,
            humidityPercentage = tadoMinuteList.sumOf { it.humidityPercentage ?: 0.0 } / tadoMinuteList.size,
            sunnyMinutes = tadoMinuteList.sumOf { it.sunnyMinutes ?: 0 },
            callForHeat = tadoMinuteList.map { it.callForHeat ?: 0 }.average().roundToInt(),
            settingTemperature = tadoMinuteList.sumOf { it.settingTemperature ?: 0.0 } / tadoMinuteList.size,
            powerOnMinutes = tadoMinuteList.sumOf { it.powerOnMinutes ?: 0 },
            weatherState = tadoMinuteList.last().weatherState,
        )
        return aggregate
    }


    private fun getTadoReportTimeUnit(localTime: LocalDateTime): TadoDayReportTimeUnit {
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

    private fun LocalDateTime.tadoTimeToLocalTime(): LocalDateTime {
        return this
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of("Europe/Berlin"))
            .toLocalDateTime()
    }

}


//    private val objectMapper = defineObjectMapper()
//
//    fun writeToFile(date: LocalDate) {
//        val response = tadoClient.getTadoHistoricalInfoAsString(date)
//        val f = File("data/tado/dayreport_$date")
//        f.writeText(response)
//    }
//
//    private fun defineObjectMapper(): ObjectMapper {
//        val objectMapper = jacksonObjectMapper()
//        objectMapper.registerModule(JavaTimeModule())
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        return objectMapper
//    }
//
//    private fun getDayReportFromFile(day: LocalDate) {
//        val jsonString = File("data/tado/dayreport_$day").bufferedReader().readLine()
//        return objectMapper.readValue(jsonString)
//    }
//
