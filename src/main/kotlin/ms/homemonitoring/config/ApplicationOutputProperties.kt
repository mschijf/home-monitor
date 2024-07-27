package ms.homemonitoring.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "output")
data class ApplicationOutputProperties(
    val baseFileName: String,
    val path: String
)
