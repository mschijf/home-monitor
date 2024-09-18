package ms.homemonitor.service

import ms.tools.splitByCondition
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class LogService {

    fun getLogs(): List<LogLine> {
        val file = File("log/home-monitor-log")
        val list = if (file.exists() && file.length() > 0) {
            file
                .readLines()
                .splitByCondition { it.startsWithTime() }
                .filter { it.isNotEmpty() }
                .map { LogLine.of(it.joinToString("\n")) }
        } else {
            emptyList()
        }
        return list
    }

    private fun String.startsWithTime(): Boolean {
        val first = this.substringBefore(" ")
        return try {
            LocalDateTime.parse(first, DateTimeFormatter.ISO_DATE_TIME)
            true
        } catch (e: Exception) {
            false
        }
    }
}

data class LogLine(
    val time: LocalDateTime,
    val type: String,
    val pid: Int,
    val application: String,
    val process: String,
    val className: String,
    val msg: String,
    val throwable: String,
    val all: String
) {

    companion object {
        fun of(logLine: String): LogLine {
            try {
                return LogLine(
                    time = stringToLocalDateTime(logLine.substring(0, 29)),
                    type = logLine.substring(30, 35).trim(),
                    pid = logLine.substring(36, 43).trim().toInt(),
                    application = logLine.substring(48, 63).trim(),
                    process = logLine.substring(66, 81).trim(),
                    className = logLine.substring(83, 124).substringAfterLast(".").trim(),
                    msg = logLine.substring(126).substringBefore('\n').trim(),
                    throwable = if (logLine.contains('\n')) logLine.substringAfter('\n').trim() else "",
                    all = logLine
                )
            } catch (e: Exception) {
                return LogLine(LocalDateTime.MIN, "", -1, "", "", "", "", "", logLine)
            }
        }

        private fun stringToLocalDateTime(stringDate: String?): LocalDateTime {
            return LocalDateTime.parse(stringDate!!, DateTimeFormatter.ISO_DATE_TIME)
        }
    }

}