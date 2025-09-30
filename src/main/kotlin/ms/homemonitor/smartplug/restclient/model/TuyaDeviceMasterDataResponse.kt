package ms.homemonitor.smartplug.restclient.model

data class TuyaDeviceMasterDataResponse(
    val result: List<TuyaDeviceMasterData>?,
    val success: Boolean,
    val tid: String?,
    val t: Long?,
)