package ms.homemonitor.power.data.repository

import ms.homemonitor.shared.summary.domain.service.WithTotals
import ms.homemonitor.power.data.model.PowerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PowerRepository: JpaRepository<PowerEntity, LocalDateTime>, WithTotals {

    @Query(value = "SELECT max(power_normal_kwh)-min(power_normal_kwh) from power where time >= :from and time <= :end", nativeQuery = true)
    fun getTotalNormalPowerBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

    @Query(value = "SELECT max(power_offpeak_kwh)-min(power_offpeak_kwh) from power where time >= :from and time <= :end", nativeQuery = true)
    fun getTotalOffpeakPowerBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

    @Query(value = "SELECT max(power_offpeak_kwh)-min(power_offpeak_kwh) + max(power_normal_kwh)-min(power_normal_kwh) from power where time >= :from and time <= :end", nativeQuery = true)
    override fun getTotalBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double
}