package ms.homemonitor.tado.repository.model

import java.io.Serializable
import java.time.LocalDateTime

data class TadoId(
    val time: LocalDateTime = LocalDateTime.MIN,
    val zoneId: Int = 1,
) : Serializable
