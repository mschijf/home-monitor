package ms.homemonitor.infra.weerlive.model

import com.fasterxml.jackson.annotation.JsonProperty

data class LiveWeer (
    @JsonProperty("plaats")
    val location: String,

    @JsonProperty("timestamp")
    val timestamp: String,

    @JsonProperty("time")
    val time: String,

    @JsonProperty("temp")
    val temp: Double,
    @JsonProperty("gtemp")
    val groundTemp: Double,

    @JsonProperty("windr")
    val windDirection: String,
    @JsonProperty("windms")
    val windForceMeterPerSecond: Double,
    @JsonProperty("windbft")
    val windForceBeafort: Double,

    @JsonProperty("luchtd")
    val pressure: Double,
)