package ms.homemonitor.tado.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.dateTimeRangeByMinute
import ms.homemonitor.tado.repository.TadoHourAggregateRepository
import ms.homemonitor.tado.repository.model.TadoHourAggregateEntity
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.tado.service.model.TadoDayReportTimeUnit
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Service
class TadoDayReportService(
    private val tadoClient: TadoClient,
    private val tadoHourAggregateRepository: TadoHourAggregateRepository,) {

    fun processHourAggregateMeasurement(day:LocalDate = LocalDate.now()) {
        getHourList(day).forEach { tadoHour ->
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

    private fun getHourList(day: LocalDate): List<TadoDayReportTimeUnit> {
        val tadoMinuteList = getMinuteList(day)
        return tadoMinuteList
            .groupBy { it.time.hour }
            .mapValues { aggregateTadoReportTimeUnitMinuteListToHour(it.value) }
            .values.toList()
    }

    private fun getMinuteList(day: LocalDate): List<TadoDayReportTimeUnit> {
        val tadoDayReport = tadoClient.getTadoHistoricalInfo(day)
        val tadoDayReportTimeUnitDetails = TadoDayReportTimeUnitDetails.of(tadoDayReport)

        return dateTimeRangeByMinute(day.atStartOfDay(), day.plusDays(1).atStartOfDay().minusSeconds(1))
            .map { time -> tadoDayReportTimeUnitDetails.getDetailsForTime(time) }
            .toList()
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
}





//    fun processHistory() {
//        val start = LocalDate.of(2024, 4, 1)
//        val end = LocalDate.now()
//        dateRangeByDay(start, end).forEach { day ->
//            println(day)
//            processHourAggregateMeasurement(day)
//        }
//    }


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
