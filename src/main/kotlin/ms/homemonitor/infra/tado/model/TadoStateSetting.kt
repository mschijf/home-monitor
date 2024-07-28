package ms.homemonitor.infra.tado.model

data class TadoStateSetting(
    val type: String,
    val power: String,
    val temperature: Double
)
