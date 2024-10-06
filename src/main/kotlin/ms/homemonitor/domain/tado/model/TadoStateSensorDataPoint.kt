package ms.homemonitor.domain.tado.model

data class TadoStateSensorDataPoint(
    val insideTemperature: TadoTemperature,
    val humidity: TadoPercentage
)
