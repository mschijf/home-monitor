package ms.homemonitor.electricity.domain.service

import ms.homemonitor.electricity.data.repository.ElectricityRepository
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.shared.summary.domain.service.SummaryService
import org.springframework.stereotype.Service

@Service
class HomeWizardElectricityService(
    private val electricityRepository: ElectricityRepository,
    private val summary: SummaryService,
) {

    fun getElectricityYearSummary(): YearSummary {
        return summary.getSummary(electricityRepository)
    }
}