package ms.homemonitor.domain.log

import ms.homemonitor.domain.log.model.LogLine
import ms.homemonitor.tools.splitByCondition
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