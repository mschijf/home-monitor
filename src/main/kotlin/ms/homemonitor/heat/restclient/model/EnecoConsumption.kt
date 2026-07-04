package ms.homemonitor.heat.restclient.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class EnecoConsumption(
    val date: LocalDateTime,
    val totalUsedGigaJoule: BigDecimal
)