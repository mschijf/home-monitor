package ms.homemonitor.tado.repository

import ms.homemonitor.tado.repository.model.TadoHourAggregateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TadoHourAggregateRepository: JpaRepository<TadoHourAggregateEntity, LocalDateTime> {
}