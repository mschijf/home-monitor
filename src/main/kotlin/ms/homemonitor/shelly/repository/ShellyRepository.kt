package ms.homemonitor.shelly.repository

import ms.homemonitor.shelly.repository.model.ShellyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ShellyRepository: JpaRepository<ShellyEntity, LocalDateTime> {

}