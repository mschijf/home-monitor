package ms.homemonitor.domain.tado.model

data class TadoPowerSetting(
    val type: String,
    val power: String,
    val temperature: Double
)
