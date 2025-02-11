package ms.homemonitor.heath.service

import jakarta.transaction.Transactional
import ms.homemonitor.heath.repository.model.HeathEntity
import ms.homemonitor.heath.repository.HeathRepository
import ms.homemonitor.heath.restclient.EnecoRestClient
import ms.homemonitor.shared.summary.service.model.YearSummary
import ms.homemonitor.shared.summary.service.SummaryService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class HeathService(
    private val heathRepository: HeathRepository,
    private val enecoRestClient: EnecoRestClient,
    private val enecoStatsService: EnecoStatsService,
    private val summary: SummaryService,
    @Value("\${home-monitor.eneco.initialDate}") private val initialDate: LocalDateTime,
    @Value("\${home-monitor.eneco.initialHeathValue}") private val initialHeathValue: BigDecimal,
) {

    private val log = LoggerFactory.getLogger(HeathService::class.java)

    fun getYearSummary(): YearSummary {
        return summary.getSummary(heathRepository)
    }

    @Transactional
    fun processMeaurement() {
        val success = updateEnecoData()
        enecoStatsService.updateEnecoStats(success)
        if (!success) {
            processFailedUpdate()
        }
    }

    private fun updateEnecoData(): Boolean {
        val beginningOfLastDay = clearLastDay()
        val newHeathRecordList = getNewDataFromDate(beginningOfLastDay.toLocalDate())
        heathRepository.saveAllAndFlush(newHeathRecordList)
        return newHeathRecordList.isNotEmpty()
    }

    private fun lastRecord(): HeathEntity {
        return heathRepository.getLastHeathEntity() ?: HeathEntity(initialDate, BigDecimal.ZERO, initialHeathValue)
    }

    private fun clearLastDay(): LocalDateTime {
        val beginningOfLastDay = LocalDateTime.of(lastRecord().time.toLocalDate(), LocalTime.MIDNIGHT)
        heathRepository.deleteHeathEntitiesByTimeGreaterThanEqual(beginningOfLastDay)
        return beginningOfLastDay
    }

    private fun getNewDataFromDate(beginningOfLastDay: LocalDate): List<HeathEntity> {
        val freshDataList = enecoRestClient.getNewDataFromEneco(beginningOfLastDay).sortedBy { it.date }
        val newHeathRecordList = freshDataList
            .map{fresh ->
                HeathEntity(
                    time = fresh.date,
                    deltaGJ = fresh.totalUsedGigaJoule,
                    heathGJ = BigDecimal.ZERO
                )
            }
            .runningFold(lastRecord()) {acc, elt ->
                HeathEntity(
                    time = elt.time,
                    deltaGJ = elt.deltaGJ,
                    heathGJ = acc.heathGJ?.plus(elt.deltaGJ!!)
                )
            }
            .drop(1)
        return newHeathRecordList
    }

    private fun processFailedUpdate() {
        val lastUpdateTime = enecoStatsService.getLastSuccessfullUpdate()
        val now = LocalDateTime.now()
        val diff = ChronoUnit.HOURS.between(lastUpdateTime, now)
        if (diff > 5) {
            log.error("Last succesfull update more than $diff hours ago. Last succesfull one was at $lastUpdateTime")
        }
    }
}