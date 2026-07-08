package ms.homemonitor.tado.restclient.model

data class TadoZone(
    val id: Int,
    val name: String,
    val type: String,
    val deviceList: List<TadoDevice>
)