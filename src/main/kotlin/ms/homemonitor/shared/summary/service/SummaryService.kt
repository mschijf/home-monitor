package ms.homemonitor.shared.summary.service

import ms.homemonitor.shared.summary.repository.RepositoryWithTotals
import ms.homemonitor.shared.summary.service.model.YearSummary
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class SummaryService {
    private val cache = mutableMapOf<RepositoryWithTotals, YearSummary>()

    @Scheduled(cron = "0 15 * * * *")
    private fun clearCache() {
        cache.clear()
    }

    fun getSummary(repositoryWithTotals: RepositoryWithTotals): YearSummary {
        return cache.getOrPut(repositoryWithTotals) { yearSummaryUntilLastHour(repositoryWithTotals)}
    }

    private fun yearSummaryUntilLastHour(repository: RepositoryWithTotals): YearSummary {
        val now = LocalDateTime.now()
        val hoursInYear = 24 * if (now.toLocalDate().isLeapYear) 366 else 365
        val thisYear = now.year
        val prevYear = thisYear - 1

        val yearStart = LocalDate.of(thisYear, 1, 1).atStartOfDay()
        val todayHour = LocalDateTime.of(thisYear, now.month, now.dayOfMonth, now.hour, 0, 0)
        val prevYearStart = LocalDate.of(prevYear, 1, 1).atStartOfDay()
        val todayHourPrevYear= LocalDateTime.of(prevYear, now.month, now.dayOfMonth, now.hour, 0, 0)

        val actualPrevYear = repository.getTotalBetweenDates(prevYearStart, yearStart)
        val actualRunningYear = repository.getTotalBetweenDates(todayHourPrevYear, todayHour)
        val actualYTDPrevYear = repository.getTotalBetweenDates(prevYearStart, todayHourPrevYear)
        val actualYTD = repository.getTotalBetweenDates(yearStart, todayHour)

        val hoursBetween = ChronoUnit.HOURS.between(yearStart, todayHour)
        val yearExpectationExtrapolate = hoursInYear * actualYTD / hoursBetween
        val yearExpectationComparedWithLastYear = (actualYTD / actualYTDPrevYear) * actualPrevYear

        return YearSummary(
            actualPrevYear, actualYTD, actualYTDPrevYear, actualRunningYear,
            yearExpectationExtrapolate, yearExpectationComparedWithLastYear
        )
    }
}