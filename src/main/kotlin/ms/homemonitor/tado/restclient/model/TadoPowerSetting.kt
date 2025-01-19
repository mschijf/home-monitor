package ms.homemonitor.tado.restclient.model

data class TadoPowerSetting(
    val type: String,
    val power: String,
    val temperature: TadoTemperature?
)
