package ms.homemonitor.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tado")
data class TadoProperties(
    val enabled: Boolean,
    val envUrl: String,
    val clientId: String,
    val username: String,
    val password: String,
    val secret: String,
    val baseRestUrl: String
)
