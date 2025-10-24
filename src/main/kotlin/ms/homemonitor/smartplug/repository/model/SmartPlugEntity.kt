package ms.homemonitor.smartplug.repository.model

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal


@Entity
@Table(name = "smart_plug")
class SmartPlugEntity(
    @EmbeddedId
    var id: SmartPlugId = SmartPlugId(),

    @Column(name = "delta_wh", nullable = true)
    var deltaWH: BigDecimal? = null,

    @Column(name = "device_id", nullable = true)
    var deviceId: String? = null,

    )