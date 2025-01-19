package ms.homemonitor.domain.tado.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ms.homemonitor.domain.tado.model.TadoDayReport
import ms.homemonitor.repository.tado.TadoEntity
import ms.homemonitor.tools.dateTimeRangeByMinute
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TadoHistoricalDataProcessor(
    private val tado: Tado) {

    private val objectMapper = defineObjectMapper()

    fun writeToFile(date: LocalDate) {
        val response = tado.getTadoHistoricalInfoAsString(date)
        val f = File("data/tado/dayreport_$date")
        f.writeText(response)
    }

    private fun defineObjectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return objectMapper
    }

    fun processHistoricalDay(day: LocalDate): List<TadoEntity> {
        val jsonString = File("data/tado/dayreport_$day").bufferedReader().readLine()
        val tadoDayReport: TadoDayReport = objectMapper.readValue(jsonString)

        val tadoDayDetails = TadoDayDetails(tadoDayReport)
        return dateTimeRangeByMinute(day.atStartOfDay(), day.plusDays(1).atStartOfDay().minusSeconds(1))
            .map { time -> tadoDayDetails.getDetailsForTimeStamp(time) }
            .groupBy { it.time.hour }
            .mapValues { it -> aggregateTadoMinuteListToHour(it.value) }
            .values.toList()
    }

    private fun aggregateTadoMinuteListToHour(tadoMinuteList: List<TadoEntity>): TadoEntity {
        val lastTado = tadoMinuteList.last()
        val lastTime = lastTado.time
        val xx = TadoEntity(
            time = LocalDateTime.of(lastTime.year, lastTime.month, lastTime.dayOfMonth, lastTime.hour, 0, 0).plusHours(1),
            insideTemperature = lastTado.insideTemperature,
            outsideTemperature = lastTado.outsideTemperature,
            humidityPercentage = lastTado.humidityPercentage,
            solarIntensityPercentage = tadoMinuteList.sumOf { it.solarIntensityPercentage?:0.0 } / tadoMinuteList.size,
            heatingPowerPercentage = tadoMinuteList.sumOf { it.heatingPowerPercentage?:0.0 } / tadoMinuteList.size,
            settingTemperature = tadoMinuteList.sumOf { it.settingTemperature?:0.0 } / tadoMinuteList.size,
            settingPowerOn = tadoMinuteList.any { it.settingPowerOn == true },
            weatherState = lastTado.weatherState
        )
        return xx
    }
}

data class TadoDayDetails(val tadoDayReport: TadoDayReport) {

    val insideTemperaturelist = tadoDayReport.measuredData.insideTemperature.dataPoints.map { it.timestamp to it.value.celsius }.sortedBy { it.first }
    val humidityPercentageList = tadoDayReport.measuredData.humidity.dataPoints.map { it.timestamp to it.value }.sortedBy { it.first }
    val settingPowerOnList = tadoDayReport.settings.dataIntervals.map { it.from to it.value.power }.sortedBy { it.first }
    val settingTemperatureList = tadoDayReport.settings.dataIntervals.map { it.from to it.value.temperature }.sortedBy { it.first }

    val outsideTemperatureList = tadoDayReport.weather.condition.dataIntervals.map { it.from to it.value.temperature }.sortedBy { it.first }
    val weatherStateList = tadoDayReport.weather.condition.dataIntervals.map { it.from to it.value.state }.sortedBy { it.first }

    val callForHeatList = tadoDayReport.callForHeat.dataIntervals.map { it.from to it.value }.sortedBy { it.first }
    val isSunnyList = tadoDayReport.weather.sunny.dataIntervals.map { it.from to it.value }.sortedBy { it.first }

    fun getDetailsForTimeStamp(localTime: LocalDateTime): TadoEntity {
        val time = localTime.minusHours(1)
        val insideTemperature = insideTemperaturelist.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second
        val humidityPercentage = humidityPercentageList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second
        val heatingPowerPercentage = when (callForHeatList.lastOrNull{ it.first.isBefore(time.plusNanos(1)) }?.second) {
            "HIGH" -> 100.0
            "MEDIUM" -> 60.0
            "LOW" -> 30.0
            "NONE" -> 0.0
            else -> throw Exception("unknown call for heat value for $time")
        }
        val settingPowerOn = (settingPowerOnList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second?:"OFF") == "ON"
        val settingTemperature = settingTemperatureList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second?.celsius?:0.0
        val outsideTemperature = outsideTemperatureList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second?.celsius!!
        val solarIntensityPercentage = if (isSunnyList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second == true) 77.0 else 0.0
        val weatherState = weatherStateList.lastOrNull { it.first.isBefore(time.plusNanos(1)) }?.second

        return TadoEntity (
            localTime, insideTemperature, humidityPercentage?.times(100.0),
            heatingPowerPercentage, settingPowerOn,
            settingTemperature, outsideTemperature,
            solarIntensityPercentage, weatherState)
    }
}