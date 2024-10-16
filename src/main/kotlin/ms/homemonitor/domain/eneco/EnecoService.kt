package ms.homemonitor.domain.eneco

import ms.homemonitor.domain.eneco.model.EnecoConsumption
import ms.homemonitor.domain.eneco.rest.Eneco
import ms.homemonitor.domain.eneco.repository.EnecoRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class EnecoService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository
) {

    private fun emptyTimeList(fromTime: LocalDateTime, toTime: LocalDateTime, plusHours: Long): List<EnecoConsumption> {
        val extraList = mutableListOf<EnecoConsumption>()
        var start = fromTime
        while (start <= toTime) {
            extraList.add(EnecoConsumption(start, BigDecimal.ZERO))
            start = start.plusHours(plusHours)
        }
        return extraList
    }

    fun getEnecoHourConsumption(from: LocalDateTime, to:LocalDateTime): List<EnecoConsumption> {
        val storedList = enecoRepository.getHourList()
            .filter { it.date in from..to }

        if (storedList.isEmpty()) {
            return emptyList()
        }

        val extraList = emptyTimeList(
            fromTime=storedList.last().date.plusDays(1),
            toTime=if (to < LocalDateTime.now()) to else LocalDateTime.now(),
            plusHours = 1)
        return (storedList + extraList)
    }

    fun getEnecoDayConsumption(from: LocalDateTime, to:LocalDateTime): List<EnecoConsumption> {
        val storedList = enecoRepository.getDayList()
            .filter { it.date in from..to }

        if (storedList.isEmpty()) {
            return emptyList()
        }

        val extraList = emptyTimeList(
            fromTime=storedList.last().date.plusDays(1),
            toTime=if (to < LocalDateTime.now()) to else LocalDateTime.now(),
            plusHours = 24)
        return (storedList + extraList)
    }

    fun getEnecoCumulativeDayConsumption(from: LocalDateTime, to:LocalDateTime): List<EnecoConsumption> {
        return enecoRepository.getDayList()
            .runningFold(EnecoConsumption(eneco.initialDate, eneco.initalStartValue)) { acc, elt -> EnecoConsumption(elt.date, acc.totalUsedGigaJoule+elt.totalUsedGigaJoule)}
            .drop(1)
            .filter { it.date in from..to  }
     }
}