package ms.homemonitor.domain.tado

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tado")
data class TadoProperties(
    val enabled: Boolean,
    val tokenUrl: String,
    val clientSecret: String,
    val clientId: String,
    val username: String,
    val password: String,
    val baseRestUrl: String
)
