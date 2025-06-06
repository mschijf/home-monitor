package ms.homemonitor.shared.tools

import java.time.LocalDate
import java.time.LocalDateTime

fun dateRangeByDay(startDate: LocalDate, endDate: LocalDate): Sequence<LocalDate> {
    return generateSequence<LocalDate>(startDate) { date -> date.plusDays(1) }.takeWhile { date -> date.isBefore(endDate) }
}

fun dateTimeRangeByHour(start: LocalDateTime, end: LocalDateTime): Sequence<LocalDateTime> {
    return generateSequence<LocalDateTime>(start) { dateTime -> dateTime.plusHours(1) }.takeWhile { dateTime -> dateTime.isBefore(end) }
}

fun dateTimeRangeByMinute(start: LocalDateTime, end: LocalDateTime): Sequence<LocalDateTime> {
    return generateSequence<LocalDateTime>(start) { dateTime -> dateTime.plusMinutes(1) }.takeWhile { dateTime -> dateTime.isBefore(end) }
}

