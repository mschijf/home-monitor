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
    @Column(name = "start_time", nullable = false, unique = true)
    val startTime: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "end_time", nullable = true)
    var endTime: LocalDateTime? = null,

    @Column(name = "duration_minutes", nullable = true)
    var durationMinutes: Int? = null,

    @Column(name = "liters", nullable = true)
    var liters: Double? = null,

    @Column(name = "heat_gj", nullable = true)
    var heatGJ: Double? = null,
)
