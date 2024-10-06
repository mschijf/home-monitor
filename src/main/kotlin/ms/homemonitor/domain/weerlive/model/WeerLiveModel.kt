package ms.homemonitor.domain.weerlive.model

import com.fasterxml.jackson.annotation.JsonProperty

data class WeerLiveModel (
    @JsonProperty("liveweer")
    val currentWeather: List<LiveWeer>,

    @JsonProperty("api")
    val api: List<ApiModel>,

    )
