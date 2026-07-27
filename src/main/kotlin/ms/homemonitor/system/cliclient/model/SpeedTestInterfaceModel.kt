package ms.homemonitor.system.cliclient.model

data class SpeedTestInterfaceModel(
    val internalIp: String,
    val name: String,
    val macAddr: String,
    val isVpn: Boolean,
    val externalIp: String,
)
