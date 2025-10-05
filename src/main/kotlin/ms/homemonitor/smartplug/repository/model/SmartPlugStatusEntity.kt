package ms.homemonitor.smartplug.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime


@Entity
@Table(name = "smart_plug_status")
class SmartPlugStatusEntity(
    @Id
    @Column(name = "time", nullable = false, unique = true)
    val time: LocalDateTime = LocalDateTime.MIN,

    @Column(name = "number_known", nullable = true)
    var numberKnown: Int? = null,

    @Column(name = "number_on_line", nullable = true)
    var numberOnLine: Int? = null,
)