package ms.homemonitor.domain.eneco

import ms.homemonitor.domain.eneco.model.EnecoConsumption
import ms.homemonitor.domain.eneco.rest.Eneco
import ms.homemonitor.domain.eneco.repository.EnecoRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class EnecoUpdateService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository
) {

    fun updateEnecoStatistics(source: String): List<EnecoConsumption> {
        val consumptionList = enecoRepository.readAll()
        val fromDate = consumptionList.lastOrNull()?.date ?: eneco.initialDate
        val freshDataList = getNewDataBySource(source,fromDate).sortedBy { it.date }

        val newList = consumptionList.filter { it.date.toLocalDate() != fromDate.toLocalDate() } + freshDataList
        enecoRepository.storeEnecoData(newList)

        return freshDataList
    }

    private fun getNewDataBySource(source: String, fromDate: LocalDateTime): List<EnecoConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoHourDataBySourcePage(source, fromDate.toLocalDate(), now.plusDays(1))
        return response
            .map{ EnecoConsumption(it.actual.date, it.actual.warmth.high) }
    }
}