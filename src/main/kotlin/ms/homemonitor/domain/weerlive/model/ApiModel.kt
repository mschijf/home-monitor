package ms.homemonitor.domain.weerlive.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiModel (
    @JsonProperty("bron")
    val source: String,

    @JsonProperty("max_verz")
    val maxNumberOfRequests: Int,

    @JsonProperty("rest_verz")
    val numberOfRequestsLeft: Int,
)