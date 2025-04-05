package ms.homemonitor.tado.service.model

import java.time.LocalDateTime

data class TadoReportTimeUnit(
    val time: LocalDateTime = LocalDateTime.MIN,
    val insideTemperature: Double?,
    val outsideTemperature: Double?,
    val humidityPercentage: Double?,
    val powerOnMinutes: Int?,
    val settingTemperature: Double?,
    val sunnyMinutes: Int?,
    val weatherState: String?,
    val callForHeat: Int?,
)