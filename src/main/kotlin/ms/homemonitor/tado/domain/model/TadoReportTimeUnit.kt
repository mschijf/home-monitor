package ms.homemonitor.tado.domain.model

import java.time.LocalDateTime

data class TadoReportTimeUnit(
    val time: LocalDateTime = LocalDateTime.MIN,
    val insideTemperature: Double?,
    val humidityPercentage: Double?,
    val settingPowerOn: Boolean?,
    val callForHeat: String?,
    val settingTemperature: Double?,
    val outsideTemperature: Double?,
    val isSunny: Boolean?,
    val weatherState: String?
)

fun callForHeatToInt(cfh: String?): Int {
    return when (cfh) {
        "HIGH" -> 30
        "MEDIUM" -> 20
        "LOW" -> 10
        "NONE" -> 0
        null -> 0
        else -> throw Exception("$cfh is an unknown call for-heat-value")
    }
}

