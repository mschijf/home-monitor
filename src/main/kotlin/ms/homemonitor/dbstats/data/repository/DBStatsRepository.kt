package ms.homemonitor.dbstats.data.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Entity
class DummyEntity { @Id private val id: Int? = null }

@Repository
interface DBStatsRepository: JpaRepository<DummyEntity, Int> {

    @Query(value = "SELECT pg_database_size(:dbName)", nativeQuery = true)
    fun getDatabaseSize(@Param("dbName") dbName: String): Long
}