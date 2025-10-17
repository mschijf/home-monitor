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

    @Column(name = "delta_kwh", nullable = true)
    var deltaKWH: BigDecimal? = null,

    @Column(name = "is_virtual", nullable = true)
    var isVirtual: Boolean? = null,

)