package ms.homemonitor.domain.eneco.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class EnecoConsumption(
    val date: LocalDateTime,
    val totalUsedGigaJoule: BigDecimal
)