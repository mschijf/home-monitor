package ms.homemonitor.tado.data.repository

import ms.homemonitor.tado.data.model.TadoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TadoRepository: JpaRepository<TadoEntity, LocalDateTime>