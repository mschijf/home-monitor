package ms.homemonitor.smartplug.repository

import ms.homemonitor.smartplug.repository.model.SmartPlugStatusEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SmartPlugStatusRepository: JpaRepository<SmartPlugStatusEntity, LocalDateTime> {
}