package ms.homemonitor.service

import ms.homemonitor.infra.eneco.rest.Eneco
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.EnecoDayConsumption
import ms.homemonitor.repository.EnecoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class EnecoService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository,
    private val measurement: MicroMeterMeasurement,
) {

    fun updateEnecoStatistics(source: String): List<EnecoDayConsumption> {
        val consumptionList = enecoRepository.readAll()
        val fromDate = consumptionList.lastOrNull()?.date ?: eneco.initialDate
        val freshDataList = getNewDataBySource(source,fromDate).sortedBy { it.date }

        val newList = consumptionList.filter { it.date.toLocalDate() != fromDate.toLocalDate() } + freshDataList
        enecoRepository.store(newList)

        val finalConsumptionSinceInitalDate = newList.sumOf { it.totalUsedGigaJoule }
        setMetrics(finalConsumptionSinceInitalDate)

        return freshDataList
    }

    private fun getNewDataBySource(source: String, fromDate: LocalDateTime): List<EnecoDayConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoHourDataBySourcePage(source, fromDate.toLocalDate(), now.plusDays(1))
        return response
            .map{ EnecoDayConsumption(it.actual.date, it.actual.warmth.high)}
    }


    private fun setMetrics(finalValue: BigDecimal) {
        measurement.setDoubleGauge("warmthStandingGJ", finalValue.toDouble())
    }

    private fun hourToDay(): List<EnecoDayConsumption> {
        return enecoRepository
            .readAll()
            .groupBy { it.date.toLocalDate() }
            .mapValues { it.value.sumOf { e -> e.totalUsedGigaJoule } }
            .map{EnecoDayConsumption(LocalDateTime.of(it.key, LocalTime.of(0,0,0)), it.value)}
    }

    fun getEnecoDayConsumption(from: LocalDateTime, to:LocalDateTime): List<EnecoDayConsumption> {
        val storedList = hourToDay()
            .filter { it.date in from..to }

        if (storedList.isEmpty()) {
            return emptyList()
        }

        val lastDate = if (to < LocalDateTime.now()) to else LocalDateTime.now()
        val extraList = mutableListOf<EnecoDayConsumption>()
        var start = storedList.last().date.plusDays(1)
        while (start <= lastDate) {
            extraList.add(EnecoDayConsumption(start, BigDecimal.ZERO))
            start = start.plusDays(1)
        }
        return (storedList + extraList)
    }

    fun getEnecoCumulativeDayConsumption(): List<EnecoDayConsumption> {
        return hourToDay()
            .runningFold(EnecoDayConsumption(eneco.initialDate, eneco.initalStartValue)) {acc, elt -> EnecoDayConsumption(elt.date, acc.totalUsedGigaJoule+elt.totalUsedGigaJoule)}
            .drop(1)
     }
}