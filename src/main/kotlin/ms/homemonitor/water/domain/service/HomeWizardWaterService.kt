package ms.homemonitor.water.domain.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.summary.domain.service.SummaryService
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.water.data.model.WaterEntity
import ms.homemonitor.water.data.repository.WaterRepository
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class HomeWizardWaterService(
    private val waterRepository: WaterRepository,
    private val summary: SummaryService,
) {
    fun getWaterYearSummary(): YearSummary {
        return summary.getSummary(waterRepository)
    }

}