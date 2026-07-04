package ms.homemonitor.heat.service

import ms.homemonitor.heat.repository.EnecoStatsRepository
import ms.homemonitor.heat.repository.model.EnecoStatsEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EnecoStatsService(
    private val enecoStatsRepository: EnecoStatsRepository,
) {
    fun updateEnecoStats(success: Boolean) {
        val record = EnecoStatsEntity(LocalDateTime.now(), success)
        enecoStatsRepository.saveAndFlush(record)
    }

    fun getLastSuccessfullUpdate(): LocalDateTime {
        return enecoStatsRepository.getLastSuccessfull()?.time ?: LocalDateTime.MIN
    }

}