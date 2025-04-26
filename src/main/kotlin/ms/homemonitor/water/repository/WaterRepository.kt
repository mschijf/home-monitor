package ms.homemonitor.water.repository

import ms.homemonitor.shared.summary.repository.RepositoryWithTotals
import ms.homemonitor.water.repository.model.WaterEntity
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
        "FROM water "

@Repository
interface WaterRepository: JpaRepository<WaterEntity, LocalDateTime>, RepositoryWithTotals {

    @Query(value = "SELECT max(water_m3)-min(water_m3) from water where time >= :from and time <= :end", nativeQuery = true)
    override fun getTotalBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): Double

    @Modifying
    @Query(value = "WITH ranked AS (" + rankedQuery + ")" +
            "DELETE FROM water " +
            "WHERE time IN ( SELECT time FROM ranked WHERE rn > 1 )" +
            "  AND time < :beforeTime;"
        , nativeQuery = true)
    fun deleteDataBeforeTime(@Param("beforeTime")beforeTime: LocalDateTime)

    @Query(value = "WITH ranked AS (" + rankedQuery + ")" +
            "SELECT count(*) FROM water " +
            "WHERE time IN ( SELECT time FROM ranked WHERE rn > 1 )" +
            "  AND time < :beforeTime;"
        , nativeQuery = true)
    fun countRecordsBeforeTime(@Param("beforeTime")beforeTime: LocalDateTime): Long
}