package ms.homemonitor.smartplug.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TuyaDeviceMasterData(
    @JsonProperty("id")
    val deviceId: String,
    @JsonProperty("customName")
    val customName: String,
)
