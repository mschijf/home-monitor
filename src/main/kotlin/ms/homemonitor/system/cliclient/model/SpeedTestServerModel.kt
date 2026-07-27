package ms.homemonitor.system.cliclient.model

data class SpeedTestServerModel(
    val id: Int,
    val host: String,
    val port: Int,
    val name: String,
    val location: String,
    val country: String,
    val ip: String,
)
