package ms.homemonitor.shared.summary.repository

import java.time.LocalDateTime

interface RepositoryWithTotals {
    fun getTotalBetweenDates(from: LocalDateTime, end: LocalDateTime): Double
}