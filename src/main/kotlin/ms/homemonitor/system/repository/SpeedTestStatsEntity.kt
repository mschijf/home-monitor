package ms.homemonitor.system.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "speedtest_stats")
class SpeedTestStatsEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "download_speed", nullable = false)
    val downloadSpeed: Double = 0.0,

    @Column(name = "upload_speed", nullable = false)
    val uploadSpeed: Double = 0.0,

    @Column(name = "download_jitter", nullable = false)
    val downloadJitter: Double = 0.0,

    @Column(name = "upload_jitter", nullable = false)
    val uploadJitter: Double = 0.0,
)
