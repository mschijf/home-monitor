package ms.homemonitor.service

import ms.homemonitor.infra.eneco.rest.Eneco
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.EnecoDayConsumption
import ms.homemonitor.repository.EnecoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class EnecoService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository,
    private val measurement: MicroMeterMeasurement,
) {

    private val initialDate = LocalDateTime.of(2024, 8, 1, 0, 0, 0)
    private val initalStartValue = BigDecimal(198.505)

    fun updateEnecoStatistics(source: String): List<EnecoDayConsumption> {
        val consumptionList = enecoRepository.readAll().sortedBy { it.date }
        val fromDate = consumptionList.lastOrNull()?.date ?: initialDate
        val freshDataList = getNewDataBySource(source,fromDate).sortedBy { it.date }
        updateEnecoStatistics(freshDataList)
        return freshDataList
    }

    private fun updateEnecoStatistics(freshDataList: List<EnecoDayConsumption>): List<EnecoDayConsumption> {
        val consumptionList = enecoRepository.readAll().sortedBy { it.date }
        enecoRepository.store(consumptionList.dropLast(1 ) + freshDataList)
        recalculatingTotal()
        return freshDataList
    }

    fun recalculatingTotal(): BigDecimal {
        val consumptionList = enecoRepository.readAll()
            .sortedBy { it.date }
        val finalConsumptionSinceInitalDate = consumptionList.sumOf { it.totalUsedGigaJoule }
        setMetrics(finalConsumptionSinceInitalDate)
        return finalConsumptionSinceInitalDate
    }

    private fun getNewDataBySource(source: String, fromDate: LocalDateTime): List<EnecoDayConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoDataBySourcePage(source, fromDate.toLocalDate(), now.plusDays(1))!!
        return response.data.usages[0].entries
            .map{ EnecoDayConsumption(it.actual.date, it.actual.warmth.high)}
    }


    private fun setMetrics(finalValue: BigDecimal) {
        measurement.setDoubleGauge("warmthStandingGJ", finalValue.toDouble())
    }

    fun getEnecoDayConsumption(): List<EnecoDayConsumption> {
        return enecoRepository
            .readAll()
            .sortedBy { it.date }
    }

    fun getEnecoCumulativeDayConsumption(): List<EnecoDayConsumption> {
        return enecoRepository
            .readAll()
            .sortedBy { it.date }
            .scan(EnecoDayConsumption(LocalDateTime.now(), initalStartValue)) {acc, elt -> EnecoDayConsumption(elt.date, acc.totalUsedGigaJoule+elt.totalUsedGigaJoule)}
            .drop(1)
    }

}