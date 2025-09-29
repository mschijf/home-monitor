package ms.homemonitor.smartplug.repository

import ms.homemonitor.heath.repository.model.HeathEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SmartPlugRepository: JpaRepository<HeathEntity, LocalDateTime> {

    @Query(value = "select smartPlug from SmartPlugEntity smartPlug where smartPlug.deviceId = :deviceId order by smartPlug.time desc limit 1")
    fun getLastSmartPlugEntity(@Param("deviceId")deviceId: String): HeathEntity?

}