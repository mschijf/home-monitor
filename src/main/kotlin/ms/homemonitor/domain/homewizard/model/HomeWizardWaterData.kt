package ms.homemonitor.domain.homewizard.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

// see https://api-documentation.homewizard.com/docs/endpoints/api-v1-data/

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
//{
//    "wifi_ssid": "MartinsKPN",
//    "wifi_strength": 100,
//    "total_liter_m3": 0.082,
//    "active_liter_lpm": 0,
//    "total_liter_offset_m3": 0
//}