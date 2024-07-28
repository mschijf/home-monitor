package ms.homemonitor.infra.tado.model

data class TadoState(
    val tadoMode: String,
    val setting: TadoStateSetting,
    //"openWindow": null,
    val activityDataPoints: TadoStateActivityDataPoint,
    val sensorDataPoints: TadoStateSensorDataPoint
)
