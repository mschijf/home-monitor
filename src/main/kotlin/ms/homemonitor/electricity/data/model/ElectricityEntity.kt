package ms.homemonitor.electricity.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "electricity")
class ElectricityEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "power_normal_kwh", nullable = true)
    var powerNormalKwh: BigDecimal? =  null,

    @Column(name = "power_offpeak_kwh", nullable = true)
    var powerOffpeakKwh: BigDecimal? =  null,
)