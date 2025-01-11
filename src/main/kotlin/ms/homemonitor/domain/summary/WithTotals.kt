package ms.homemonitor.domain.summary

import java.time.LocalDateTime

interface WithTotals {
    fun getTotalBetweenDates(from: LocalDateTime, end: LocalDateTime): Double
}