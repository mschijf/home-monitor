package ms.homemonitor.tado.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tado")
class TadoEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "zone_id", nullable = false)
    val zoneId: String = "1",

    @Column(name = "inside_temperature", nullable = true)
    var insideTemperature: Double? =  null,

    @Column(name = "humidity_percentage", nullable = true)
    var humidityPercentage: Double? =  null,

    @Column(name = "heating_power_percentage", nullable = true)
    var heatingPowerPercentage: Double? =  null,

    @Column(name = "setting_power_on", nullable = true)
    var settingPowerOn: Boolean? =  null,

    @Column(name = "setting_temperature", nullable = true)
    var settingTemperature: Double? =  null,
)