package ms.homemonitor.domain.homewizard.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface HeathRepository: JpaRepository<HeathEntity, LocalDateTime>