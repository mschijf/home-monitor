package ms.homemonitor.domain.homewizard

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "homewizard")
data class HomeWizardProperties(
    val enabled: Boolean,
    val energyBaseRestUrl: String,
    val waterBaseRestUrl: String,
)

