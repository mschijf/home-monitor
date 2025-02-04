package ms.homemonitor.heath.domain.service

import ms.homemonitor.heath.data.model.EnecoStatsEntity
import ms.homemonitor.heath.data.repository.EnecoStatsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class EnecoStatsService(
    private val enecoStatsRepository: EnecoStatsRepository,
) {
    fun updateEnecoStats(success: Boolean) {
        val record = enecoStatsRepository
            .findById(LocalDate.now())
            .orElse(EnecoStatsEntity(day=LocalDate.now(), success = 0, failed = 0, last=null))
        if (success) {
            record.success++
            record.last = LocalDateTime.now()
        } else {
            record.failed++
        }
        enecoStatsRepository.saveAndFlush(record)
    }

    fun getLastSuccessfullUpdate(): LocalDateTime {
        return enecoStatsRepository.getLastSuccessfull()?.last ?: LocalDateTime.MIN
    }

}