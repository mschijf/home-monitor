package ms.homemonitor.heath.domain.service

import jakarta.transaction.Transactional
import ms.homemonitor.heath.data.model.EnecoStatsEntity
import ms.homemonitor.heath.data.model.HeathEntity
import ms.homemonitor.heath.data.repository.EnecoStatsRepository
import ms.homemonitor.heath.data.repository.HeathRepository
import ms.homemonitor.heath.restclient.EnecoRestClient
import ms.homemonitor.shared.admin.data.model.AdminKey
import ms.homemonitor.shared.admin.data.repository.AdminRepositoryTool
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.summary.domain.service.SummaryService
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
    private val adminRepositoryTool: AdminRepositoryTool,
    private val enecoStatsRepository: EnecoStatsRepository,
    private val summary: SummaryService,
    @Value("\${eneco.initialDate}") private val initialDate: LocalDateTime,
    @Value("\${eneco.initialHeathValue}") private val initialHeathValue: BigDecimal,
) {

    private val log = LoggerFactory.getLogger(HeathService::class.java)

    fun getYearSummary(): YearSummary {
        return summary.getSummary(heathRepository)
    }

    @Transactional
    fun processMeaurement() {
        val success = updateEnecoData()
        updateEnecoStats(success)
        if (success) {
            updateAdminRecord()
        } else {
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

    private fun updateEnecoStats(success: Boolean) {
        val record = enecoStatsRepository
            .findById(LocalDate.now())
            .orElse(EnecoStatsEntity(day=LocalDate.now(), success = 0, failed = 0))
        if (success) {
            record.success++
            record.last = LocalDateTime.now()
        } else {
            record.failed++
        }
        enecoStatsRepository.saveAndFlush(record)
    }

    private fun processFailedUpdate() {
        val lastUpdateTime = getLastUpdateTimestamp()
        val now = LocalDateTime.now()
        val diff = ChronoUnit.HOURS.between(lastUpdateTime, now)
        if (diff > 5) {
            log.error("Last succesfull update more than $diff hours ago. Last succesfull one was at $lastUpdateTime")
        }
    }
    private fun updateAdminRecord() {
        adminRepositoryTool
            .updateAdminTimestampRecord(AdminKey.LAST_ENECO_UPDATE, LocalDateTime.now())
    }

    private fun getLastUpdateTimestamp(): LocalDateTime {
        return adminRepositoryTool
            .getAdminTimestamp(AdminKey.LAST_ENECO_UPDATE) ?: LocalDateTime.MIN
    }

}