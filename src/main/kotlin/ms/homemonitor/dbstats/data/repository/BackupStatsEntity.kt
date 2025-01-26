package ms.homemonitor.dbstats.data.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "backup_stats")
class BackupStatsEntity(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: Int = 1,

    @Column(name = "oldest", nullable = true)
    var oldest: LocalDateTime? =  null,

    @Column(name = "last", nullable = true)
    var last: LocalDateTime? =  null,

    @Column(name = "size", nullable = true)
    var size: Long? =  null,

    @Column(name = "free_space", nullable = true)
    var freeSpace: Long? =  null,
)