package ms.homemonitor.power.domain.service

import ms.homemonitor.power.data.repository.PowerRepository
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.summary.domain.service.SummaryService
import org.springframework.stereotype.Service

@Service
class HomeWizardPowerService(
    private val powerRepository: PowerRepository,
    private val summary: SummaryService,
) {

    fun getPowerYearSummary(): YearSummary {
        return summary.getSummary(powerRepository)
    }
}