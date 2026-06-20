package ms.homemonitor.water.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "shower_usage")
class ShowerUsageEntity(
    @Id
    @Column(name = "date", nullable = false, unique = true)
    val date: LocalDate,

    @Column(name = "shower_count", nullable = false)
    val showerCount: Int,

    @Column(name = "total_liters", nullable = false)
    val totalLiters: Double,

    @Column(name = "total_heat_gj", nullable = false)
    val totalHeatGJ: Double,
)
