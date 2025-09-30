package ms.homemonitor.smartplug.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TuyaAuth(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("expire_time")
    val expireTime: Int,

    @JsonProperty("uid")
    val uid: String,
)