package ms.homemonitor.heath.data.repository

import ms.homemonitor.heath.data.model.EnecoStatsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface EnecoStatsRepository: JpaRepository<EnecoStatsEntity, LocalDate>