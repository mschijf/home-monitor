package ms.homemonitor.tado.repository

import ms.homemonitor.tado.repository.model.TadoHourAggregateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TadoHourAggregateRepository: JpaRepository<TadoHourAggregateEntity, LocalDateTime> {

    @Query(value = "SELECT * from tado where time >= :from and time <= :end", nativeQuery = true)
    fun findBetweenDates(@Param("from")from: LocalDateTime, @Param("end")end: LocalDateTime): List<TadoHourAggregateEntity>
}