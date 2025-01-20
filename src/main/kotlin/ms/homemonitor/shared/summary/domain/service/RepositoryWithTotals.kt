package ms.homemonitor.shared.summary.domain.service

import java.time.LocalDateTime

interface RepositoryWithTotals {
    fun getTotalBetweenDates(from: LocalDateTime, end: LocalDateTime): Double
}