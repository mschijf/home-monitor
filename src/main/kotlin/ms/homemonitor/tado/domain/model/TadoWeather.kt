package ms.homemonitor.tado.domain.model

data class TadoWeather(
    val solarIntensity: TadoPercentage,
    val outsideTemperature: TadoTemperature,
    val weatherState: TadoWeatherState
)
