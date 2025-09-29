package ms.homemonitor.smartplug.restclient.model

data class TuyaAuthResponse(
    val result: TuyaAuth?,
    val success: Boolean,
    val tid: String?,
)