package ms.homemonitor.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "admin")
class AdminEntity(
    @Id
    @Column(name = "id", nullable = false, unique = true)
    val id: Int = 0,

    @Column(name = "last_eneco_import", nullable = false, unique = true)
    var lastEnecoImport: LocalDateTime = LocalDateTime.MIN,
)