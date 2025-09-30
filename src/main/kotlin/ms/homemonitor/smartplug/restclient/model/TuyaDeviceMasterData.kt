package ms.homemonitor.smartplug.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TuyaDeviceMasterData(
    @JsonProperty("custom_name")
    val customName: String,
    @JsonProperty("id")
    val deviceId: String,
    @JsonProperty("ip")
    val ip: String,
    @JsonProperty("name")
    val productName: String,
)
