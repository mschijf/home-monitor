package ms.homemonitor.smartplug.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
class SmartPlugId() : Serializable {

    @Column(name = "device_id", nullable = false, length = 32)
    var deviceId: String? = null

    @Column(name = "time", nullable = false)
    var time: LocalDateTime? = null

    constructor(deviceId: String, time: LocalDateTime) : this() {
        this.deviceId = deviceId
        this.time = time
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other.javaClass != javaClass) return false
        other as SmartPlugId
        return deviceId == other.deviceId && time == other.time
    }

    override fun hashCode(): Int {
        var result = deviceId?.hashCode() ?: 0
        result = 31 * result + (time?.hashCode() ?: 0)
        return result
    }
}