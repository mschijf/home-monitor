package ms.homemonitor.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "output")
data class ApplicationOutputProperties(
    val path: String
)
