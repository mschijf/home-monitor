package ms.homemonitor.water.data.repository

import ms.homemonitor.shared.summary.domain.service.WithTotals
import ms.homemonitor.water.data.model.WaterEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WaterRepository: JpaRepository<WaterEntity, LocalDateTime>, WithTotals {

    @Query(value = "SELECT max(water_m3)-min(water_m3) from water where time >= :from and time <= :end", nativeQuery = true)
    override fun getTotalBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

}