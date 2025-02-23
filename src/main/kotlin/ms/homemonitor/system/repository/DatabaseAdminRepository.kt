package ms.homemonitor.system.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface DatabaseAdminRepository: JpaRepository<DummyEntity, Int> {

    @Query(value = "SELECT pg_database_size(:dbName)", nativeQuery = true)
    fun getDatabaseSize(@Param("dbName") dbName: String): Long
}

@Entity
data class DummyEntity (@Id val id: Int = 1)