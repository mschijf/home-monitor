package ms.homemonitor.tado.repository

import ms.homemonitor.tado.repository.model.TadoTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TadoTokenRepository: JpaRepository<TadoTokenEntity, LocalDateTime> {

    @Query(value = "SELECT * from tado_token order by time desc limit 1", nativeQuery = true)
    fun readLast(): List<TadoTokenEntity>
}