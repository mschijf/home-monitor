package ms.homemonitor.smartplug.restclient.model

data class TuyaData(
    val deviceId: String,
    val hasMore: Boolean,
    val logs: List<TuyaDataDetail>,
    val total: Int,
)
