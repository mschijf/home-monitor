package ms.homemonitor.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "raspberrypi")
data class RaspberryPiProperties(
    val enabled: Boolean,
)
