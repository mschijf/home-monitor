package ms.homemonitor.tado.domain.model

data class TadoResponseModel(
    val tadoState: TadoState,
    val weather: TadoWeather
)