package ms.homemonitor.electricity.repository

import ms.homemonitor.shared.summary.repository.RepositoryWithTotals
import ms.homemonitor.electricity.repository.model.ElectricityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ElectricityRepository: JpaRepository<ElectricityEntity, LocalDateTime>, RepositoryWithTotals {

    @Query(value = "SELECT max(power_offpeak_kwh)-min(power_offpeak_kwh) + max(power_normal_kwh)-min(power_normal_kwh) from electricity where time >= :from and time <= :end", nativeQuery = true)
    override fun getTotalBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

}