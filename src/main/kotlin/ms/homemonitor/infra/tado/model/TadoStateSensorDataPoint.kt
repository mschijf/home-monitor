package ms.homemonitor.infra.tado.model

data class TadoStateSensorDataPoint(
    val insideTemperature: TadoTemperature,
    val humidity: TadoPercentage
)
