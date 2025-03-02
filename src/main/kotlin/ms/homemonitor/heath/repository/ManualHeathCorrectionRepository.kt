package ms.homemonitor.heath.repository

import ms.homemonitor.heath.repository.model.ManualHeathCorrectionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ManualHeathCorrectionRepository: JpaRepository<ManualHeathCorrectionEntity, LocalDateTime> {
    @Query(value = "select heath " +
            "from ManualHeathCorrectionEntity heath " +
            "where heath.time between :time1 and :time2 " +
            "order by heath.time desc limit 1")
    fun getLastCorrectionBetween(@Param("time1") time1: LocalDateTime,
                                 @Param("time2")time2: LocalDateTime): ManualHeathCorrectionEntity?
}