package ms.homemonitor.domain.raspberrypi

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "raspberrypi")
data class RaspberryPiProperties(
    val enabled: Boolean,
)
