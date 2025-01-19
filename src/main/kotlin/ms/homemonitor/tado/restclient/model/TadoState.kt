package ms.homemonitor.tado.restclient.model

data class TadoState(
    val tadoMode: String,
    val setting: TadoPowerSetting,
    //"openWindow": null,
    val activityDataPoints: TadoStateActivityDataPoint,
    val sensorDataPoints: TadoStateSensorDataPoint
)
