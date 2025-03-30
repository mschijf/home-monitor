package ms.homemonitor.tado.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TadoDeviceAuthorization(
    @JsonProperty("device_code")
    val deviceCode: String,

    @JsonProperty("expires_in")
    val expiresIn: String,

    @JsonProperty("interval")
    val interval: Int,

    @JsonProperty("user_code")
    val userCode: String,

    @JsonProperty("verification_uri")
    val verificationUri: String,

    @JsonProperty("verification_uri_complete")
    val verificationUriComplete: String,
)