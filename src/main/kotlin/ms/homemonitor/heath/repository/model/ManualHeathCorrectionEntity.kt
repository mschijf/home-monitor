package ms.homemonitor.heath.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "manual_heath_correction")
class ManualHeathCorrectionEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.now(),

    @Column(name = "heath_gj", nullable = true)
    var heathGJ: BigDecimal? =  null
)