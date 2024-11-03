package ms.homemonitor.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface HeathRepository: JpaRepository<HeathEntity, LocalDateTime> {

    @Query(value = "select heath from HeathEntity heath order by heath.time desc limit 1")
    fun getLastHeathEntity(): HeathEntity

    fun deleteHeathEntitiesByTimeGreaterThanEqual(dateTime: LocalDateTime)

}