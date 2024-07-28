package ms.homemonitor.tado.model

data class TadoStateSensorDataPoint(
    val insideTemperature: TadoStateInsideTemperature,
    val humidity: TadoStateHumidity
)
