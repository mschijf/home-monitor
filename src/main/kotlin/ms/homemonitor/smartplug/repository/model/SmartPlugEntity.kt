package ms.homemonitor.smartplug.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime


@Entity
@Table(name = "smart_plug")
class SmartPlugEntity() {
    @Id
    @Column(name = "device_id", nullable = false)
    val deviceId: String = ""

    @Id
    @Column(name = "time", nullable = false)
    val time: LocalDateTime = LocalDateTime.MIN

    @Column(name = "delta_kwh", nullable = true)
    var deltaKWH: BigDecimal? = null

    @Column(name = "power_kwh", nullable = true)
    var powerKWH: BigDecimal? = null
}