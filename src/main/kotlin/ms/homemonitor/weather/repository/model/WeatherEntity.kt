package ms.homemonitor.weather.repository.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "weather")
class WeatherEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "outside_temperature", nullable = true)
    var outsideTemperature: Double? =  null,

    @Column(name = "humidity_percentage", nullable = true)
    var humidityPercentage: Double? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condition_id", nullable = true)
    var condition: WeatherConditionEntity? = null,

    @Column(name = "wind_kph", nullable = true)
    var windKph: Double? = null,

    @Column(name = "wind_dir", nullable = true)
    var windDirection: String? = null,

    @Column(name = "pressure_mb", nullable = true)
    var pressureMb: Double? = null,

    @Column(name = "precip_mm", nullable = true)
    var precipMm: Double? = null, //neerslag

    @Column(name = "cloud", nullable = true)
    var cloud: Double? = null,

    @Column(name = "uv", nullable = true)
    var uv: Double? = null,
)