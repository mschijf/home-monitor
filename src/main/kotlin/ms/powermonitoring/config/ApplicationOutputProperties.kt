package ms.powermonitoring.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "output")
data class ApplicationOutputProperties(
    val enabled: Boolean,
    val variableTimeFetchRateInMilliseconds: Int,
    val variableTimeFileName: String,
    val hourFileName: String,
    val dayFileName: String,
    val path: String
)
