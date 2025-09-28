package ms.homemonitor.tuya.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TuyaAuthResponse(
    val result: TuyaAuth?,
    val success: Boolean,
    val tid: String?,
)