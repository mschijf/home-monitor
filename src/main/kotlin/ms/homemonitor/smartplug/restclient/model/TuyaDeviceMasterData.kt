package ms.homemonitor.smartplug.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TuyaDeviceMasterData(
    @JsonProperty("id")
    val deviceId: String,
    @JsonProperty("customName")
    val customName: String,
    @JsonProperty("isOnline")
    val isOnline: Boolean,
    @JsonProperty("timeZone")
    val timeZone: String
)
