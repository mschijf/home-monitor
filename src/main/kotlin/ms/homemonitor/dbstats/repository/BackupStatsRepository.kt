package ms.homemonitor.dbstats.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BackupStatsRepository: JpaRepository<BackupStatsEntity, Int> {

}