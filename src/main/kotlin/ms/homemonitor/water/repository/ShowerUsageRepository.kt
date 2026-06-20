package ms.homemonitor.water.repository

import ms.homemonitor.water.repository.model.ShowerUsageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ShowerUsageRepository : JpaRepository<ShowerUsageEntity, LocalDateTime>
