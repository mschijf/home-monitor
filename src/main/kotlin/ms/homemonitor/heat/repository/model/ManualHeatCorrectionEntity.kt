package ms.homemonitor.heat.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "manual_heath_correction")
class ManualHeatCorrectionEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.now(),

    @Column(name = "heath_gj", nullable = true)
    var heatGJ: BigDecimal? =  null,

    @Column(name = "heath_gj_beforecorrection", nullable = true)
    var heatGJBeforeCorrection: BigDecimal? =  null

)