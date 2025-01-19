package ms.homemonitor.eneco.restclient.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class EnecoConsumption(
    val date: LocalDateTime,
    val totalUsedGigaJoule: BigDecimal
)