package ms.homemonitor.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WaterRepository: JpaRepository<WaterEntity, LocalDateTime>