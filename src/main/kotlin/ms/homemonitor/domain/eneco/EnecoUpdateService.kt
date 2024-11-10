package ms.homemonitor.domain.eneco

import jakarta.transaction.Transactional
import ms.homemonitor.domain.eneco.model.EnecoConsumption
import ms.homemonitor.domain.eneco.rest.Eneco
import ms.homemonitor.repository.AdminEntity
import ms.homemonitor.repository.AdminRepository
import ms.homemonitor.repository.HeathEntity
import ms.homemonitor.repository.HeathRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class EnecoUpdateService(
    private val eneco: Eneco,
    private val heathRepository: HeathRepository,
    private val adminRepository: AdminRepository
) {

    private val log = LoggerFactory.getLogger(EnecoUpdateService::class.java)

    @Transactional
    fun updateEnecoStatistics(source: String): List<EnecoConsumption> {
        val lastRecord = heathRepository.getLastHeathEntity()
        val beginningOfLastDay = LocalDateTime.of(lastRecord.time.toLocalDate(), LocalTime.MIDNIGHT)
        heathRepository.deleteHeathEntitiesByTimeGreaterThanEqual(beginningOfLastDay)
        val newLastRecord = heathRepository.getLastHeathEntity()

        val freshDataList = getNewDataBySource(source,beginningOfLastDay.toLocalDate()).sortedBy { it.date }

        val newHeathRecordList = freshDataList
            .map{fresh -> HeathEntity(time=fresh.date, deltaGJ = fresh.totalUsedGigaJoule, heathGJ = BigDecimal.ZERO)}
            .runningFold(newLastRecord) {acc, elt -> HeathEntity(time=elt.time, deltaGJ = elt.deltaGJ, heathGJ = acc.heathGJ?.plus(elt.deltaGJ!!)) }
            .drop(1)

        heathRepository.saveAllAndFlush(newHeathRecordList)

        val lastUpdate = adminRepository.findById(0).orElse(AdminEntity(id=0, lastEnecoImport = LocalDateTime.now()))
        lastUpdate.lastEnecoImport = LocalDateTime.now()
        adminRepository.saveAndFlush(lastUpdate)


        return freshDataList
    }

    private fun getNewDataBySource(source: String, fromDate: LocalDate): List<EnecoConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoHourDataBySourcePage(source, fromDate, now.plusDays(1))
        return response
            .map{ EnecoConsumption(it.actual.date, it.actual.warmth.high) }
    }
}