package ms.homemonitor.service

import ms.homemonitor.infra.eneco.model.EnecoDayConsumption
import ms.homemonitor.infra.eneco.rest.Eneco
import ms.homemonitor.repository.EnecoRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class EnecoUpdateService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository
) {

    fun updateEnecoStatistics(source: String): List<EnecoDayConsumption> {
        val consumptionList = enecoRepository.readAll()
        val fromDate = consumptionList.lastOrNull()?.date ?: eneco.initialDate
        val freshDataList = getNewDataBySource(source,fromDate).sortedBy { it.date }

        val newList = consumptionList.filter { it.date.toLocalDate() != fromDate.toLocalDate() } + freshDataList
        enecoRepository.storeEnecoData(newList)

        return freshDataList
    }

    private fun getNewDataBySource(source: String, fromDate: LocalDateTime): List<EnecoDayConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoHourDataBySourcePage(source, fromDate.toLocalDate(), now.plusDays(1))
        return response
            .map{ EnecoDayConsumption(it.actual.date, it.actual.warmth.high) }
    }

}