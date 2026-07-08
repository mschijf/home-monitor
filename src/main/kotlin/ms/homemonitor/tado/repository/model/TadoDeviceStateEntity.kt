package ms.homemonitor.tado.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tado_device_state")
class TadoDeviceStateEntity(
    @Id
    @Column(name = "serial_number", nullable = false, length = 32)
    val serialNumber: String,

    @Column(name = "zone_id", nullable = false)
    val zoneId: Int,

    @Column(name = "zone_name", nullable = true, length = 16)
    val zoneName: String? = null,

    @Column(name = "battery_state", nullable = true, length = 16)
    var batteryState: String? = null,
)
