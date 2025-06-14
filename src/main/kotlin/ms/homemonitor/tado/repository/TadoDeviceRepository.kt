package ms.homemonitor.tado.repository

import ms.homemonitor.tado.repository.model.TadoDeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TadoDeviceRepository: JpaRepository<TadoDeviceEntity, LocalDateTime> {

    @Modifying
    @Query(value = "DELETE from tado_device_info where time < :beforeTime", nativeQuery = true)
    fun deleteDataBeforeTime(@Param("beforeTime")beforeTime: LocalDateTime)
}