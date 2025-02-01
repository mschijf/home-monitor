package ms.homemonitor.heath.data.repository

import ms.homemonitor.heath.data.model.EnecoStatsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface EnecoStatsRepository: JpaRepository<EnecoStatsEntity, LocalDate> {

    @Query(value = "select stats from EnecoStatsEntity stats where stats.last != null order by stats.day desc ")
    fun getLastSuccessfull(): EnecoStatsEntity?
}