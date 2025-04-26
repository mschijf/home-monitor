package ms.homemonitor.electricity.repository

import ms.homemonitor.shared.summary.repository.RepositoryWithTotals
import ms.homemonitor.electricity.repository.model.ElectricityEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

private const val rankedQuery = "" +
        "SELECT time, " +
        "       ROW_NUMBER() OVER ( " +
        "                    PARTITION BY DATE_TRUNC('hour', time) " +
        "                    ORDER BY time ASC " +
        "                    ) AS rn " +
        "FROM electricity "


@Repository
interface ElectricityRepository: JpaRepository<ElectricityEntity, LocalDateTime>, RepositoryWithTotals {

    @Query(value = "SELECT max(power_offpeak_kwh)-min(power_offpeak_kwh) + max(power_normal_kwh)-min(power_normal_kwh) from electricity where time >= :from and time <= :end", nativeQuery = true)
    override fun getTotalBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

    @Modifying
    @Query(value = "WITH ranked AS (" + rankedQuery + ")" +
            "DELETE FROM electricity " +
            "WHERE time IN ( SELECT time FROM ranked WHERE rn > 1 )" +
            "  AND time < :beforeTime;"
        , nativeQuery = true)
    fun deleteDataBeforeTime(@Param("beforeTime")beforeTime: LocalDateTime)

    @Query(value = "WITH ranked AS (" + rankedQuery + ")" +
            "SELECT count(*) FROM electricity " +
            "WHERE time IN ( SELECT time FROM ranked WHERE rn > 1 )" +
            "  AND time < :beforeTime;"
        , nativeQuery = true)
    fun countRecordsBeforeTime(@Param("beforeTime")beforeTime: LocalDateTime): Long


}