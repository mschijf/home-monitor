package ms.homemonitor.tuya.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TuyaDataResponse(
    val result: TuyaData?,
    val success: Boolean,
    val tid: String?,
    val t: Long?,
)