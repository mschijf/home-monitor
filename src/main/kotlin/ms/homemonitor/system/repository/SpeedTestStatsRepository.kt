package ms.homemonitor.system.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface SpeedTestStatsRepository : JpaRepository<SpeedTestStatsEntity, LocalDateTime>
