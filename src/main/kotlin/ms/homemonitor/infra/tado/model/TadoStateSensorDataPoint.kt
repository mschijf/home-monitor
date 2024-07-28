package ms.homemonitor.infra.tado.model

data class TadoStateSensorDataPoint(
    val insideTemperature: TadoStateInsideTemperature,
    val humidity: TadoStateHumidity
)
