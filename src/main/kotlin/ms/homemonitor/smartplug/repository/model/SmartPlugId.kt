package ms.homemonitor.smartplug.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
class SmartPlugId() : Serializable {

    @Column(name = "name", nullable = false, length = 32)
    var name: String? = null

    @Column(name = "time", nullable = false)
    var time: LocalDateTime? = null

    constructor(name: String, time: LocalDateTime) : this() {
        this.name = name
        this.time = time
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other.javaClass != javaClass) return false
        other as SmartPlugId
        return name == other.name && time == other.time
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (time?.hashCode() ?: 0)
        return result
    }
}