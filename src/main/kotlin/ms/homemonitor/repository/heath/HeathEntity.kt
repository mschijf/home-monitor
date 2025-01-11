package ms.homemonitor.repository.heath

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "heath")
class HeathEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "delta_gj", nullable = true)
    var deltaGJ: BigDecimal? =  null,

    @Column(name = "heath_gj", nullable = true)
    var heathGJ: BigDecimal? =  null
)