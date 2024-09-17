package ms.homemonitor.service

import ms.homemonitor.infra.eneco.rest.Eneco
import ms.homemonitor.repository.EnecoDayConsumption
import ms.homemonitor.repository.EnecoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class EnecoService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository
) {

    fun getEnecoHourConsumption(from: LocalDateTime, to:LocalDateTime): List<EnecoDayConsumption> {
        val storedList = enecoRepository.getHourList()
            .filter { it.date in from..to }

        if (storedList.isEmpty()) {
            return emptyList()
        }

        val lastDate = if (to < LocalDateTime.now()) to else LocalDateTime.now()
        val extraList = mutableListOf<EnecoDayConsumption>()
        var start = storedList.last().date.plusHours(1)
        while (start <= lastDate) {
            extraList.add(EnecoDayConsumption(start, BigDecimal.ZERO))
            start = start.plusHours(1)
        }
        return (storedList + extraList)
    }

    fun getEnecoDayConsumption(from: LocalDateTime, to:LocalDateTime): List<EnecoDayConsumption> {
        val storedList = enecoRepository.getDayList()
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
        return enecoRepository.getDayList()
            .runningFold(EnecoDayConsumption(eneco.initialDate, eneco.initalStartValue)) {acc, elt -> EnecoDayConsumption(elt.date, acc.totalUsedGigaJoule+elt.totalUsedGigaJoule)}
            .drop(1)
     }
}