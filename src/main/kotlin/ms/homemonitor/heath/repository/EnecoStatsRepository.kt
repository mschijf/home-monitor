package ms.homemonitor.heath.repository

import ms.homemonitor.heath.repository.model.EnecoStatsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EnecoStatsRepository: JpaRepository<EnecoStatsEntity, LocalDateTime> {

    @Query(value = "select stats from EnecoStatsEntity stats where stats.success = true order by stats.time desc limit 1")
    fun getLastSuccessfull(): EnecoStatsEntity?
}