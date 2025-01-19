package ms.homemonitor.tado.restclient.model

data class TadoResponseModel(
    val tadoState: TadoState,
    val weather: TadoWeather
)