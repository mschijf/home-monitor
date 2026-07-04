package ms.homemonitor.heat.repository

import ms.homemonitor.shared.summary.repository.RepositoryWithTotals
import ms.homemonitor.heat.repository.model.HeatEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface HeatRepository: JpaRepository<HeatEntity, LocalDateTime>, RepositoryWithTotals {

    @Query(value = "select heath from HeatEntity heath order by heath.time desc limit 1")
    fun getLastHeatEntity(): HeatEntity?

    fun deleteHeatEntitiesByTimeGreaterThanEqual(dateTime: LocalDateTime)

    fun findByTimeBetweenOrderByTime(from: LocalDateTime, until: LocalDateTime): List<HeatEntity>

    @Query(value = "SELECT max(heath_gj)-min(heath_gj) from heath where time >= :from and time <= :end", nativeQuery = true)
    override fun getTotalBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

}