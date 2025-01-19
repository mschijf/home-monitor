package ms.homemonitor.tado.domain.model

data class TadoStateSensorDataPoint(
    val insideTemperature: TadoTemperature,
    val humidity: TadoPercentage
)
