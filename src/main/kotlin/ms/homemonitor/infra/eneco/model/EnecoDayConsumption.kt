package ms.homemonitor.infra.eneco.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

data class EnecoDayConsumption(
    @JsonFormat(shape = JsonFormat.Shape.STRING)//, pattern =  "yyyy-MM-ddTHH:mm:ssX")
    @JsonProperty("date")
    val date: LocalDateTime,
    @JsonProperty("totalUsedGigaJoule")
    val totalUsedGigaJoule: BigDecimal
)