package ms.homemonitor.smartplug.restclient.model

data class TuyaDataResponse(
    val result: TuyaData?,
    val success: Boolean,
    val tid: String?,
    val t: Long?,
)