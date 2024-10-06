package ms.homemonitor.application

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "data-output")
data class DataOutputProperties(
    val path: String
)
