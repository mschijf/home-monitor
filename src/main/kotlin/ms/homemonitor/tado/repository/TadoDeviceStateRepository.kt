package ms.homemonitor.tado.repository

import ms.homemonitor.tado.repository.model.TadoDeviceStateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TadoDeviceStateRepository : JpaRepository<TadoDeviceStateEntity, String>
