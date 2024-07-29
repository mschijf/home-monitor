package ms.homemonitor.infra.tado.model

data class TadoResponseModel(
    val tadoState: TadoState,
    val weather: TadoWeather
)