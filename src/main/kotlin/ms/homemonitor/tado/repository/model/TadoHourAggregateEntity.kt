package ms.homemonitor.tado.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tado_hour_aggregate")
class TadoHourAggregateEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "inside_temperature", nullable = true)
    var insideTemperature: Double? =  null,

    @Column(name = "outside_temperature", nullable = true)
    var outsideTemperature: Double? =  null,

    @Column(name = "humidity_percentage", nullable = true)
    var humidityPercentage: Double? =  null,

    @Column(name = "power_on_minutes", nullable = true)
    var powerOnMinutes: Int? =  null,

    @Column(name = "setting_temperature", nullable = true)
    var settingTemperature: Double? =  null,

    @Column(name = "sunny_minutes", nullable = true)
    var sunnyMinutes: Int? =  null,

    @Column(name = "weather_state", nullable = true)
    var weatherState: String? =  null,

    @Column(name = "call_for_heat", nullable = true)
    var callForHeat: Int? =  null,
)

