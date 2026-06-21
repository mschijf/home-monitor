package ms.homemonitor.weather.repository.model

import jakarta.persistence.*

@Entity
@Table(name = "weather_condition")
class WeatherConditionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Column(name = "text", nullable = false, unique = true)
    val text: String = ""
)
