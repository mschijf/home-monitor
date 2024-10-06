package ms.homemonitor.domain.tado.model

data class TadoState(
    val tadoMode: String,
    val setting: TadoPowerSetting,
    //"openWindow": null,
    val activityDataPoints: TadoStateActivityDataPoint,
    val sensorDataPoints: TadoStateSensorDataPoint
)
