package ms.homemonitor.tado.domain.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ms.homemonitor.shared.tools.dateTimeRangeByMinute
import ms.homemonitor.tado.domain.model.TadoReportTimeUnit
import ms.homemonitor.tado.domain.model.callForHeatToInt
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
            insideTemperature = lastTado.insideTemperature,
            outsideTemperature = lastTado.outsideTemperature,
            humidityPercentage = lastTado.humidityPercentage,
            isSunny = tadoMinuteList.count { it.isSunny == true } > (tadoMinuteList.size / 2),
            callForHeat = when((1.0 * tadoMinuteList.sumOf { callForHeatToInt( it.callForHeat )} / tadoMinuteList.size ).roundToInt()) {
                in 0..4 -> "NONE"
                in 5..14 -> "LOW"
                in 15..24 -> "MEDIUM"
                in 25 .. 999999 -> "HIGH"
                else -> "NONE"
            },
            settingTemperature = tadoMinuteList.sumOf { it.settingTemperature ?: 0.0 } / tadoMinuteList.size,
            settingPowerOn = tadoMinuteList.any { it.settingPowerOn == true },
            weatherState = lastTado.weatherState
        )
        return aggregate
    }
}