package ms.homemonitor.water.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "shower_usage")
class ShowerUsageEntity(
    @Id
    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,

    @Column(name = "end_time", nullable = false)
    val endTime: LocalDateTime,

    @Column(name = "duration_minutes", nullable = false)
    val durationMinutes: Int,

    @Column(name = "liters", nullable = false)
    val liters: Double,

    @Column(name = "heat_gj", nullable = false)
    val heatGJ: Double,
)
