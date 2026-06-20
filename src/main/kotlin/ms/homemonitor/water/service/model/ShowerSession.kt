package ms.homemonitor.water.service.model

import java.time.LocalDateTime

data class ShowerSession(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val durationMinutes: Int,
    val liters: Double,
    val heatGJ: Double?,
)
