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

    fun getBackupStats() : List<BackupStats> {
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
                .sortedBy { it.dateTime }
        } catch (e: Exception) {
            log.error("Couldn't retrieve backup list, caused by ${e.message}")
            emptyList()
        }
    }

    fun getFreeBackupSpace(): Long {
        val output = try {
            commandExecutor.execCommand("./dropbox_uploader.sh", arrayListOf("space"))
        } catch (e: Exception) {
            log.error("Couldn't retrieve backup space, caused by ${e.message}")
            listOf(
                "Dropbox Uploader v1.0",
                "",
                "",
                "",
                "Quota:\t8576 Mb",
                "Used:\t158 Mb",
                "Free:\t-0 Mb"
            )
        }
        return output
            .filter { it.contains("Free:") }
            .first()
            .split("\\s+".toRegex())[1]
            .toLong()
    }
}