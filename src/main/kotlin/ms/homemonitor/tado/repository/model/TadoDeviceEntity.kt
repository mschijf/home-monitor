package ms.homemonitor.tado.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tado_device_info")
class TadoDeviceEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "battery_state", nullable = true)
    var batteryState: String? =  null,
)