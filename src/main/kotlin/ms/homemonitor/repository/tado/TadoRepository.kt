package ms.homemonitor.repository.tado

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TadoRepository: JpaRepository<TadoEntity, LocalDateTime>