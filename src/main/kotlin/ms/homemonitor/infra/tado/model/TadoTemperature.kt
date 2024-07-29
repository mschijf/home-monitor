package ms.homemonitor.infra.tado.model

data class TadoTemperature(
    val celsius: Double,
    val fahrenheit: Double,
    val type: String
)
