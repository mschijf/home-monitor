package ms.homemonitor.domain.log.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LogLine(
    val time: LocalDateTime,
    val timeStr: String,
    val type: String,
    val pid: Int,
    val application: String,
    val process: String,
    val className: String,
    val msg: String,
    val throwable: String
) {

//    2024-09-22T16:59:48.423+02:00  WARN 539802 --- [home-monitoring] [nio-8080-exec-9] .w.s.m.s.DefaultHandlerExceptionResolver : Resolved [org.springframework.web.context.request.async.AsyncRequestNotUsableException: ServletOutputStream failed to write: java.io.IOException: Broken pipe]
//    2024-09-22T17:03:31.386+02:00  INFO 998 --- [home-monitoring] [           main] m.homemonitor.HomeMonitorApplicationKt   : Starting HomeMonitorApplicationKt v4.7.0 using Java 21.0.4 with PID 998 (/home/martinschijf/home-monitor/home-monitor-4.7.0.jar started by martinschijf in /home/martinschijf/home-monitor)

    companion object {
        fun of(logLine: String): LogLine {
            val firstPart = logLine.substringBefore(" --- ")
            val lastPart = logLine.substringAfter(" --- ")
            try {
                val time = stringToLocalDateTime(firstPart.substring(0, 29))
                return LogLine(
                    time = time,
                    timeStr = formatTime(time),
                    type = firstPart.substring(30, 35).trim(),
                    pid = firstPart.substring(36).trim().toInt(),
                    application = lastPart.substring(1, 16).trim(),
                    process = lastPart.substring(19, 34).trim(),
                    className = lastPart.substring(36, 77).substringAfterLast(".").trim(),
                    msg = lastPart.substring(79).substringBefore('\n').trim(),
                    throwable = if (lastPart.contains('\n')) lastPart.substringAfter('\n').trim() else ""
                )
            } catch (e: Exception) {
                return LogLine(LocalDateTime.MIN, "", "",-1, "", "", "", msg=logLine, "")
            }
        }

        private fun stringToLocalDateTime(stringDate: String?): LocalDateTime {
            return LocalDateTime.parse(stringDate!!, DateTimeFormatter.ISO_DATE_TIME)
        }

        private fun formatTime(time: LocalDateTime): String {
            return time.format(DateTimeFormatter.ofPattern("d MMM, uuuu @ HH:mm:ss"))
        }
    }

}