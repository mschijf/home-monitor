package ms.homemonitor.smartplug.repository

import ms.homemonitor.smartplug.repository.model.SmartPlugEntity
import ms.homemonitor.smartplug.repository.model.SmartPlugId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SmartPlugRepository: JpaRepository<SmartPlugEntity, SmartPlugId> {

    @Query(value = "select smartPlug from SmartPlugEntity smartPlug where smartPlug.id.name = :name order by smartPlug.id.time desc limit 1")
    fun getLastSmartPlugEntityByName(@Param("name")name: String): SmartPlugEntity?

    @Query(value = "select smartPlug from SmartPlugEntity smartPlug where smartPlug.deviceId = :deviceId order by smartPlug.id.time desc limit 1")
    fun getLastSmartPlugEntityByDeviceId(@Param("deviceId")deviceId: String): SmartPlugEntity?

    @Query(value = "select smartPlug from SmartPlugEntity smartPlug order by smartPlug.id.time desc limit 1")
    fun getLastSmartPlugEntity(): SmartPlugEntity?

    @Query(value = "" +
            "select smartPlug from SmartPlugEntity smartPlug " +
            "where smartPlug.id.name = :name and smartPlug.id.time between :startTime and :endTime " +
            "order by smartPlug.id.time asc ")
    fun getHistoricalSmartPlugEntitiesByName(
        @Param("name")name: String,
        @Param("startTime")startTime: LocalDateTime,
        @Param("endTime")endTime: LocalDateTime): List<SmartPlugEntity>

    @Modifying
    @Query(value = "refresh materialized view electricity_hour_detail", nativeQuery = true)
    fun refreshElectricityDetail()
}