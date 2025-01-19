package ms.homemonitor.dbstats.commandline

import ms.homemonitor.dbstats.domain.model.BackupStats
import ms.homemonitor.shared.tools.commandline.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DbStats(
    private val commandExecutor: CommandExecutor,
    @Value("\${dbstats.backupListFileName}") private val backupListFileName: String,
    @Value("\${dbstats.dropbox_uploader}") private val dropboxUploader: String) {

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
            commandExecutor.execCommand(dropboxUploader, arrayListOf("space"))
        } catch (e: Exception) {
            log.error("Couldn't retrieve backup space, caused by ${e.message}")
            listOf("Free: -1 MB")
        }
        return output
            .first { it.contains("Free:") }
            .split("\\s+".toRegex())[1]
            .toLong()
    }
}