package ms.homemonitor.infra.tado.model

data class TadoWeather(
    val solarIntensity: TadoPercentage,
    val outsideTemperature: TadoTemperature,
)
