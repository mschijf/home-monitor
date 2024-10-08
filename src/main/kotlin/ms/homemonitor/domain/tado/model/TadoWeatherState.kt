package ms.homemonitor.domain.tado.model

import java.time.LocalDateTime

data class TadoWeatherState(
    val type: String,
    val value: String,
    val timestamp: LocalDateTime
)
