package ms.homemonitor.heath.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "eneco_stats")
class EnecoStatsEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.now(),

    @Column(name = "success", nullable = true)
    var success: Boolean =  false,
)