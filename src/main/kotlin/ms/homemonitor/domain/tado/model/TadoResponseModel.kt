package ms.homemonitor.domain.tado.model

data class TadoResponseModel(
    val tadoState: TadoState,
    val weather: TadoWeather
)