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

    @Scheduled(cron = "0 */5 * * * *")
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
        val remainderLastYear = repository.getTotalBetweenDates(todayHourPrevYear, yearStart)

        val hoursBetween = ChronoUnit.HOURS.between(yearStart, todayHour)
        val yearExpectationExtrapolate = hoursInYear * actualYTD / hoursBetween

        val nWeeks = 13L
        val pastWeeks = now.minusWeeks(nWeeks)
        val nowYearAgo = now.minusYears(1)
        val pastWeeksYearAgo = nowYearAgo.minusWeeks(nWeeks)
        val actualPastweeks = repository.getTotalBetweenDates(pastWeeks, now)
        val actualPastweeksPrevYear = repository.getTotalBetweenDates(pastWeeksYearAgo, nowYearAgo)
        val yearExpectationComparedWithLastYear = (actualPastweeks / actualPastweeksPrevYear) * actualPrevYear

        val newPrognose1 = (actualPrevYear / actualYTDPrevYear) * actualYTD

        val trend = 0.5 * getFactor(repository, now, 28) +
                0.3 * getFactor(repository, now, 90) +
                0.2 * getFactor(repository, now, now.dayOfYear.toLong())
        val newPrognose2 = actualYTD + remainderLastYear * trend



        return YearSummary(
            actualPrevYear, actualYTD, actualYTDPrevYear, actualRunningYear,
            yearExpectationExtrapolate, yearExpectationComparedWithLastYear, newPrognose1, newPrognose2
        )
    }

    private fun getFactor(repository: RepositoryWithTotals, now: LocalDateTime, nDays: Long): Double {
        val pastDays = now.minusDays(nDays)
        val nowYearAgo = now.minusYears(1)
        val pastDaysYearAgo = nowYearAgo.minusDays(nDays)
        val actualPastDays = repository.getTotalBetweenDates(pastDays, now)
        val actualPastDaysPrevYear = repository.getTotalBetweenDates(pastDaysYearAgo, nowYearAgo)
        return (actualPastDays / actualPastDaysPrevYear)
    }
}