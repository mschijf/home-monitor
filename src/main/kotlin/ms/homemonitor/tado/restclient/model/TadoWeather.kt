package ms.homemonitor.tado.restclient.model

data class TadoWeather(
    val solarIntensity: TadoPercentage,
    val outsideTemperature: TadoTemperature,
    val weatherState: TadoWeatherState
)
