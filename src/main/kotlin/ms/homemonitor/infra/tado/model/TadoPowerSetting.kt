package ms.homemonitor.infra.tado.model

data class TadoPowerSetting(
    val type: String,
    val power: String,
    val temperature: Double
)
