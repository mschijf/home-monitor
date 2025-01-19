package ms.homemonitor.water.restclient.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class HomeWizardWaterData(
    @JsonProperty("time")
    val time: LocalDateTime = LocalDateTime.now(),

    @JsonProperty(value = "wifi_strength")
    val wifiStrength: Int,

    @JsonProperty(value = "total_liter_m3")
    val totalLiterM3: BigDecimal,

    @JsonProperty(value = "active_liter_lpm")
    val activeLiterLpm: BigDecimal,

    @JsonProperty(value = "total_liter_offset_m3")
    val totalLiterOffsetM3: BigDecimal,
)