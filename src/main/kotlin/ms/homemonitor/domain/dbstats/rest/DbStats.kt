package ms.homemonitor.domain.dbstats.rest

import ms.homemonitor.domain.dbstats.model.BackupStats
import ms.homemonitor.tools.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DbStats(
    private val commandExecutor: CommandExecutor,
    @Value("\${dbstats.backupListFileName}") private val backupListFileName: String) {

    private val log = LoggerFactory.getLogger(DbStats::class.java)

    fun getBackupStatsOrNull() : BackupStats? {
        return try {
            commandExecutor.execCommand("cat", arrayListOf(backupListFileName))
                .filter { it.endsWith("postgres") }
                .map {
                    val fields = it.split("\\s+".toRegex())
                    val year = fields[3].substring(0, 4).toInt()
                    val month = fields[3].substring(4, 6).toInt()
                    val day = fields[3].substring(6, 8).toInt()
                    val hour = fields[3].substring(9, 11).toInt()
                    val minute = fields[3].substring(11, 13).toInt()
                    val second = fields[3].substring(13, 15).toInt()
                    BackupStats(fields[2].toLong(), LocalDateTime.of(year, month, day, hour, minute, second))
                }
                .minByOrNull { it.dateTime }!!
        } catch (e: Exception) {
            log.error("Couldn't retrieve backup list, caused by ${e.message}")
            null
        }
    }
}