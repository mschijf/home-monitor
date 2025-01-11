package ms.homemonitor.repository.heath

import ms.homemonitor.domain.summary.WithTotals
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface HeathRepository: JpaRepository<HeathEntity, LocalDateTime>, WithTotals {

    @Query(value = "select heath from HeathEntity heath order by heath.time desc limit 1")
    fun getLastHeathEntity(): HeathEntity?

    fun deleteHeathEntitiesByTimeGreaterThanEqual(dateTime: LocalDateTime)

    @Query(value = "SELECT max(heath_gj)-min(heath_gj) from heath where time >= :from and time <= :end", nativeQuery = true)
    override fun getTotalBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

}