package ms.homemonitor.domain.eneco

import jakarta.transaction.Transactional
import ms.homemonitor.domain.eneco.rest.Eneco
import ms.homemonitor.domain.summary.SummaryService
import ms.homemonitor.domain.summary.model.YearSummary
import ms.homemonitor.repository.admin.AdminEntity
import ms.homemonitor.repository.admin.AdminKey
import ms.homemonitor.repository.admin.AdminRepository
import ms.homemonitor.repository.heath.HeathEntity
import ms.homemonitor.repository.heath.HeathRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class EnecoService(
    private val eneco: Eneco,
    private val heathRepository: HeathRepository,
    private val adminRepository: AdminRepository,
    private val summary: SummaryService,
    @Value("\${eneco.enabled}") private val enabled: Boolean,
    @Value("\${eneco.initialDate}") private val initialDate: LocalDateTime,
    @Value("\${eneco.initialHeathValue}") private val initialHeathValue: BigDecimal,
) {
    private val log = LoggerFactory.getLogger(EnecoService::class.java)

    @Transactional
    @Scheduled(cron = "0 0 0/2 * * *")
    fun updateEnecoStatistics() {
        if (!enabled)
            return

        updateEnecoData()
        updateAdminRecord()
    }

    private fun updateEnecoData() {
        val beginningOfLastDay = clearLastDay()
        val newHeathRecordList = getNewDataFromDate(beginningOfLastDay.toLocalDate())
        heathRepository.saveAllAndFlush(newHeathRecordList)
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
        val freshDataList = eneco.getNewDataFromEneco(beginningOfLastDay).sortedBy { it.date }
        val newHeathRecordList = freshDataList
            .map{fresh -> HeathEntity(time=fresh.date, deltaGJ = fresh.totalUsedGigaJoule, heathGJ = BigDecimal.ZERO)}
            .runningFold(lastRecord()) {acc, elt -> HeathEntity(time=elt.time, deltaGJ = elt.deltaGJ, heathGJ = acc.heathGJ?.plus(elt.deltaGJ!!)) }
            .drop(1)
        return newHeathRecordList
    }

    private fun updateAdminRecord() {
        val lastUpdate = adminRepository
            .findById(AdminKey.LAST_ENECO_UPDATE.toString())
            .orElse(AdminEntity(key= AdminKey.LAST_ENECO_UPDATE.toString(), value = LocalDateTime.now().toString()))

        lastUpdate.value = LocalDateTime.now().toString()
        adminRepository.saveAndFlush(lastUpdate)
    }

    fun getYearSummary(): YearSummary {
        return summary.getSummary(heathRepository)
    }
}