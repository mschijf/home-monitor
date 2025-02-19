package ms.homemonitor.shared.admin.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "admin")
class AdminEntity(
    @Id
    @Column(name = "key", nullable = false, unique = true)
    val key: String = "UNKNOWN",

    @Column(name = "type", nullable = true, unique = false)
    var type: String? = null,

    @Column(name = "value", nullable = true, unique = false)
    var value: String? = null,

)