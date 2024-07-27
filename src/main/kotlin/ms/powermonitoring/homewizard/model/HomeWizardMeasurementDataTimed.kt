package ms.powermonitoring.homewizard.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class HomeWizardMeasurementDataTimed(
    @JsonProperty("time")
    val time: LocalDateTime,
    @JsonProperty("data")
    val data: HomeWizardMeasurementData

)
