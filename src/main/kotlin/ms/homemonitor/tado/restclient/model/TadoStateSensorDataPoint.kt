package ms.homemonitor.tado.restclient.model

data class TadoStateSensorDataPoint(
    val insideTemperature: TadoTemperature,
    val humidity: TadoPercentage
)
