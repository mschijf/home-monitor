package ms.homemonitor.tado.domain.model

import java.time.LocalDateTime

data class TadoReportTimeUnit(
    val time: LocalDateTime = LocalDateTime.MIN,
    val insideTemperature: Double?,
    val humidityPercentage: Double?,
    val settingPowerOn: Boolean?,
    val callForHeat: Int?,
    val settingTemperature: Double?,
    val outsideTemperature: Double?,
    val sunnyMinutes: Int?,
    val weatherState: String?
)
