package ms.homemonitor.shared.summary.service

import ms.homemonitor.shared.summary.repository.RepositoryWithTotals
import ms.homemonitor.shared.summary.service.model.YearSummary
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SummaryService {
    private val cache = mutableMapOf<RepositoryWithTotals, YearSummary>()

    @Scheduled(cron = "0 1 0 * * *")
    private fun clearCache() {
        cache.clear()
    }

    fun getSummary(repositoryWithTotals: RepositoryWithTotals): YearSummary {
        return cache.getOrPut(repositoryWithTotals) { YearSummary.Companion.of(repositoryWithTotals)}
    }
}