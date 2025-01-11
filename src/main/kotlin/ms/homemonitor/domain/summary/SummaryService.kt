package ms.homemonitor.domain.summary

import ms.homemonitor.domain.summary.model.YearSummary
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SummaryService {
    private val cache = mutableMapOf<WithTotals, YearSummary>()

    @Scheduled(cron = "0 1 0 * * *")
    private fun clearCache() {
        cache.clear()
    }

    fun getSummary(withTotals: WithTotals): YearSummary {
        return cache.getOrPut(withTotals) {YearSummary.of(withTotals)}
    }
}