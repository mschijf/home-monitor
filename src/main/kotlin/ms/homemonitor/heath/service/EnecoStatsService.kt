package ms.homemonitor.heath.service

import ms.homemonitor.heath.repository.model.EnecoStatsEntity
import ms.homemonitor.heath.repository.EnecoStatsRepository
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