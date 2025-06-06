package ms.homemonitor.tado.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TadoOAuth(
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("token_type")
    val tokenType: String,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("expires_in")
    val expiresIn: String,

    @JsonProperty("scope")
    val scope: String,

    @JsonProperty("userId")
    val userId: String,

//    @JsonProperty("jti")
//    val jti: String,
)
