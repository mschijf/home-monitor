package ms.homemonitor.shared.admin.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "admin_timestamp")
class AdminTimestampEntity(
    @Id
    @Column(name = "key", nullable = false, unique = true)
    val key: String = "UNKNOWN",

    @Column(name = "time", nullable = true, unique = false)
    var time: LocalDateTime? = null,

)