package ms.homemonitor.domain.weerlive

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "weerlive")
data class WeerLiveProperties(
    val enabled: Boolean,
    val baseRestUrl: String,
    val apiKey: String,
    val locationCoordinateN: Double,
    val locationCoordinateE: Double
)

