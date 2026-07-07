package ms.homemonitor.tado.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tado_outside")
class TadoOutsideEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "outside_temperature", nullable = true)
    var outsideTemperature: Double? =  null,

    @Column(name = "solar_intensity_percentage", nullable = true)
    var solarIntensityPercentage: Double? =  null,
)