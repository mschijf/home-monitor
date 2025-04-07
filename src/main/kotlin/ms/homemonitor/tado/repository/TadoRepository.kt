package ms.homemonitor.tado.repository

import ms.homemonitor.tado.repository.model.TadoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TadoRepository: JpaRepository<TadoEntity, LocalDateTime> {

    @Modifying
    @Query(value = "DELETE from tado where time < :beforeTime", nativeQuery = true)
    fun deleteDataBeforeTime(@Param("beforeTime")beforeTime: LocalDateTime)

    @Query(value = "SELECT count(*) from tado where time < :beforeTime", nativeQuery = true)
    fun countRecordsBeforeTime(@Param("beforeTime")beforeTime: LocalDateTime): Long

}