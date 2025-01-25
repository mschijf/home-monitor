package ms.homemonitor.tado.domain.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ms.homemonitor.shared.tools.dateTimeRangeByMinute
import ms.homemonitor.tado.domain.model.TadoReportTimeUnit
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.tado.restclient.model.TadoDayReport
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Service
class TadoHistoricalDataProcessor(
    private val tadoClient: TadoClient
) {

    private val objectMapper = defineObjectMapper()

    fun writeToFile(date: LocalDate) {
        val response = tadoClient.getTadoHistoricalInfoAsString(date)
        val f = File("data/tado/dayreport_$date")
        f.writeText(response)
    }

    private fun defineObjectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return objectMapper
    }

    fun processHistoricalDay(day: LocalDate, useFile: Boolean = false): List<TadoReportTimeUnit> {
        val tadoDayReport: TadoDayReport = if (useFile) {
            val jsonString = File("data/tado/dayreport_$day").bufferedReader().readLine()
            objectMapper.readValue(jsonString)
        } else {
            tadoClient.getTadoHistoricalInfo(day)
        }

        val tadoDayDetails = TadoDayReportDetails(tadoDayReport)
        return dateTimeRangeByMinute(day.atStartOfDay(), day.plusDays(1).atStartOfDay().minusSeconds(1))
            .map { time -> tadoDayDetails.getTadoReportTimeUnit(time) }
            .groupBy { it.time.hour }
            .mapValues { it -> aggregateTadoReportTimeUnitMinuteListToHour(it.value) }
            .values.toList()
    }

    private fun aggregateTadoReportTimeUnitMinuteListToHour(tadoMinuteList: List<TadoReportTimeUnit>): TadoReportTimeUnit {
        val lastTado = tadoMinuteList.last()
        val lastTime = lastTado.time
        val aggregate = TadoReportTimeUnit(
            time = LocalDateTime.of(lastTime.year, lastTime.month, lastTime.dayOfMonth, lastTime.hour, 0, 0).plusHours(1),
            insideTemperature = tadoMinuteList.sumOf { it.insideTemperature ?: 0.0 } / tadoMinuteList.size,
            outsideTemperature = tadoMinuteList.sumOf { it.outsideTemperature ?: 0.0 } / tadoMinuteList.size,
            humidityPercentage = tadoMinuteList.sumOf { it.humidityPercentage ?: 0.0 } / tadoMinuteList.size,
            sunnyMinutes = tadoMinuteList.sumOf { it.sunnyMinutes ?: 0 },
            callForHeat = tadoMinuteList.map { it.callForHeat ?: 0 }.average().roundToInt(),
            settingTemperature = tadoMinuteList.sumOf { it.settingTemperature ?: 0.0 } / tadoMinuteList.size,
            settingPowerOn = tadoMinuteList.any { it.settingPowerOn == true },
            weatherState = lastTado.weatherState
        )
        return aggregate
    }
}