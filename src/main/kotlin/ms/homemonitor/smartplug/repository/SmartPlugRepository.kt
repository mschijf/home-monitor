package ms.homemonitor.smartplug.repository

import ms.homemonitor.smartplug.repository.model.SmartPlugEntity
import ms.homemonitor.smartplug.repository.model.SmartPlugId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SmartPlugRepository: JpaRepository<SmartPlugEntity, SmartPlugId> {

    @Query(value = "select smartPlug from SmartPlugEntity smartPlug where smartPlug.id.name = :name order by smartPlug.id.time desc limit 1")
    fun getLastSmartPlugEntity(@Param("name")name: String): SmartPlugEntity?

}