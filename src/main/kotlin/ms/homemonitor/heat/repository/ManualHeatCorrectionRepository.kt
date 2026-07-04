package ms.homemonitor.heat.repository

import ms.homemonitor.heat.repository.model.ManualHeatCorrectionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ManualHeatCorrectionRepository: JpaRepository<ManualHeatCorrectionEntity, LocalDateTime> {
    @Query(value = "select heath " +
            "from ManualHeatCorrectionEntity heath " +
            "where heath.time between :time1 and :time2 " +
            "order by heath.time desc limit 1")
    fun getLastCorrectionBetween(@Param("time1") time1: LocalDateTime,
                                 @Param("time2")time2: LocalDateTime): ManualHeatCorrectionEntity?
}