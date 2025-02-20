package ms.homemonitor.log.service

import ms.homemonitor.log.service.model.LogLine
import ms.homemonitor.shared.tools.splitByCondition
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class LogService(@Value("\${home-monitor.log.location}") private val logFile: String) {

    fun getLogs(): List<LogLine> {
        val file = File(logFile)
        val list = if (file.exists() && file.length() > 0) {
            file
                .readLines()
                .splitByCondition { it.startsWithTime() }
                .filter { it.isNotEmpty() }
                .map { LogLine.Companion.of(it.joinToString("\n")) }
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
        } catch (_: Exception) {
            false
        }
    }
}