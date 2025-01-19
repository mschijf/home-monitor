package ms.homemonitor.tado.restclient.model

import java.time.LocalDateTime

data class TadoWeatherState(
    val type: String,
    val value: String,
    val timestamp: LocalDateTime
)
