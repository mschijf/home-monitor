package ms.homemonitor.domain.homewizard

import org.springframework.boot.context.properties.ConfigurationProperties
import java.math.BigDecimal

@ConfigurationProperties(prefix = "homewizard")
data class HomeWizardProperties(
    val enabled: Boolean,
    val energyBaseRestUrl: String,
    val waterBaseRestUrl: String,
    val initialWaterValue: BigDecimal
)

