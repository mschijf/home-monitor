package ms.homemonitor.shared.summary.service.model

import ms.homemonitor.shared.summary.repository.RepositoryWithTotals
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class YearSummary(
    val actualPreviousYear: Double,
    val actualYTD: Double,
    val actualYTDPreviousYear: Double,
    val actualRunningYear: Double,
    val yearExpectationExtrapolate: Double,
    val yearExpectationComparedWithLastYear: Double) {

    companion object {
        fun of(repository: RepositoryWithTotals): YearSummary {
            val today = LocalDate.now()
            val thisYear = today.year
            val yearStart = LocalDate.of(thisYear, 1, 1)

            val prevYear = thisYear - 1
            val prevYearStart = LocalDate.of(prevYear, 1, 1)
            val todayPrevYear = LocalDate.of(prevYear, today.month, today.dayOfMonth)

            val actualPrevYear = repository.getTotalBetweenDates(prevYearStart.atStartOfDay(), yearStart.atStartOfDay())
            val actualRunningYear = repository.getTotalBetweenDates(todayPrevYear.atStartOfDay(), today.atStartOfDay())
            val actualYTDPrevYear = repository.getTotalBetweenDates(prevYearStart.atStartOfDay(), todayPrevYear.atStartOfDay())
            val actualYTD = repository.getTotalBetweenDates(yearStart.atStartOfDay(), today.atStartOfDay())


            val daysBetween = ChronoUnit.DAYS.between(yearStart, today)
            val daysInYear = if (LocalDate.now().isLeapYear) 366 else 365
            val yearExpectationExtrapolate = daysInYear * actualYTD / daysBetween
            val yearExpectationComparedWithLastYear = (actualYTD / actualYTDPrevYear) * actualPrevYear
            return YearSummary(
                actualPrevYear, actualYTD, actualYTDPrevYear, actualRunningYear,
                yearExpectationExtrapolate, yearExpectationComparedWithLastYear
            )
        }

        fun ofHour(repository: RepositoryWithTotals): YearSummary {
            val now = LocalDateTime.now()
            val thisYear = now.year
            val yearStart = LocalDate.of(thisYear, 1, 1).atStartOfDay()
            val todayHour = LocalDateTime.of(thisYear, 1, 1, now.hour, 0, 0)

            val prevYear = thisYear - 1
            val prevYearStart = LocalDate.of(prevYear, 1, 1).atStartOfDay()
            val todayHourPrevYear= LocalDateTime.of(prevYear, now.month, now.dayOfMonth, now.hour, 0, 0)


            val actualPrevYear = repository.getTotalBetweenDates(prevYearStart, yearStart)
            val actualRunningYear = repository.getTotalBetweenDates(todayHourPrevYear, todayHour)
            val actualYTDPrevYear = repository.getTotalBetweenDates(prevYearStart, todayHourPrevYear)
            val actualYTD = repository.getTotalBetweenDates(yearStart, todayHour)

            val hoursBetween = ChronoUnit.HOURS.between(yearStart, todayHour)
            val hoursInYear = 24 * if (LocalDate.now().isLeapYear) 366 else 365
            val yearExpectationExtrapolate = hoursInYear * actualYTD / hoursBetween
            val yearExpectationComparedWithLastYear = (actualYTD / actualYTDPrevYear) * actualPrevYear
            return YearSummary(
                actualPrevYear, actualYTD, actualYTDPrevYear, actualRunningYear,
                yearExpectationExtrapolate, yearExpectationComparedWithLastYear
            )
        }

    }
}