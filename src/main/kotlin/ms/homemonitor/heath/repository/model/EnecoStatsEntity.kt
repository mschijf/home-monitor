package ms.homemonitor.heath.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "eneco_stats")
class EnecoStatsEntity(
    @Id
    @Column(name = "day", nullable = false, unique = true)
    val day: LocalDate = LocalDate.now(),

    @Column(name = "success", nullable = true)
    var success: Int =  0,

    @Column(name = "failed", nullable = true)
    var failed: Int =  0,

    @Column(name = "last", nullable = true)
    var last: LocalDateTime? =  null
)