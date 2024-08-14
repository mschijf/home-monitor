package ms.homemonitor.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "eneco")
data class EnecoProperties(
    val enabled: Boolean,
    val username: String,
    val password: String,
)
