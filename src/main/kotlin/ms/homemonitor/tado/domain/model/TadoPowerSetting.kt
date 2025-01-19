package ms.homemonitor.tado.domain.model

data class TadoPowerSetting(
    val type: String,
    val power: String,
    val temperature: TadoTemperature?
)
