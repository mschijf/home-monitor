package ms.homemonitor.tado.restclient.model

data class TadoDevice (
    val deviceType: String,
    val serialNo: String,
    val shortSerialNo: String,
    val currentFwVersion: String,
    val batteryState: String?
)