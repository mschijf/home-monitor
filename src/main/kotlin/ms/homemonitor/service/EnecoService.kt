package ms.homemonitor.service

import ms.homemonitor.infra.eneco.rest.Eneco
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.EnecoDayConsumption
import ms.homemonitor.repository.EnecoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class EnecoService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository,
    private val measurement: MicroMeterMeasurement,
) {

    private val initialDate = LocalDate.of(2024, 8, 1)

    fun updateEnecoStatistics(source: String? = null): BigDecimal {
        val consumptionList = enecoRepository.readAll()
            .sortedBy { it.date }
        val freshDataList = if (source == null)
            getNewData(consumptionList.lastOrNull()?.date ?: initialDate).sortedBy { it.date }
        else
            getNewDataBySource(source,consumptionList.lastOrNull()?.date ?: initialDate).sortedBy { it.date }

        val updatedList = enecoRepository.store(consumptionList.dropLast(1 ) + freshDataList)
        val finalConsumptionSinceInitalDate = updatedList.sumOf { it.totalUsedGigaJoule }
        setMetrics(finalConsumptionSinceInitalDate)
        return finalConsumptionSinceInitalDate
    }

    fun recalculatingTotal(): BigDecimal {
        val consumptionList = enecoRepository.readAll()
            .sortedBy { it.date }
        val finalConsumptionSinceInitalDate = consumptionList.sumOf { it.totalUsedGigaJoule }
        setMetrics(finalConsumptionSinceInitalDate)
        return finalConsumptionSinceInitalDate
    }

    private fun getNewData(fromDate: LocalDate): List<EnecoDayConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoDataByScraping(fromDate, now.plusDays(1))!!
        return response.data.usages[0].entries
            .map{ EnecoDayConsumption(it.actual.date.toLocalDate(), it.actual.warmth.high)}
    }

    private fun getNewDataBySource(source: String, fromDate: LocalDate): List<EnecoDayConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoDataBySourcePage(source, fromDate, now.plusDays(1))!!
        return response.data.usages[0].entries
            .map{ EnecoDayConsumption(it.actual.date.toLocalDate(), it.actual.warmth.high)}
    }


    private fun setMetrics(finalValue: BigDecimal) {
        measurement.setDoubleGauge("warmthStandingGJ", finalValue.toDouble())
    }
}