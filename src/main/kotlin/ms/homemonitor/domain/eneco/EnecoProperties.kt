package ms.homemonitor.domain.eneco

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "eneco")
data class EnecoProperties(
    val enabled: Boolean,
)
