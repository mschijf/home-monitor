package ms.homemonitor.dbstats.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BackupStatsRepository: JpaRepository<BackupStatsEntity, Int> {

    @Query(value = "SELECT pg_database_size(:dbName)", nativeQuery = true)
    fun getDatabaseSize(@Param("dbName") dbName: String): Long
}