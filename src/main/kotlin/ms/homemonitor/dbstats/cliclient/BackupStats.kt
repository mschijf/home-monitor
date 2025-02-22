package ms.homemonitor.dbstats.cliclient

import ms.homemonitor.dbstats.cliclient.model.BackupStatsModel
import ms.homemonitor.shared.tools.commandline.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BackupStats(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.dbstats.dropbox_uploader}") private val dropboxUploader: String
) {

    private val log = LoggerFactory.getLogger(BackupStats::class.java)

    fun getBackupStats(): List<BackupStatsModel> {
        return try {
            commandExecutor.execCommand(dropboxUploader, arrayListOf("list", "Backup/home-monitor/"))
                .filter { it.endsWith("postgres") }
                .map { it.toBackupStats() }
                .sortedBy { it.dateTime }
        } catch (e: Exception) {
            log.error("Couldn't retrieve backup list, caused by ${e.message}")
            emptyList()
        }
    }

    fun getFreeBackupSpace(): Long {
        return try {
            commandExecutor.execCommand(dropboxUploader, arrayListOf("space"))
                .first { it.contains("Free:") }
                .split("\\s+".toRegex())[1]
                .toLong()
        } catch (e: Exception) {
            log.error("Couldn't retrieve backup space, caused by ${e.message}")
            -1L
        }
    }

    private fun String.toBackupStats(): BackupStatsModel {
        val fields = this.split("\\s+".toRegex())
        val year = fields[3].substring(0, 4).toInt()
        val month = fields[3].substring(4, 6).toInt()
        val day = fields[3].substring(6, 8).toInt()
        val hour = fields[3].substring(9, 11).toInt()
        val minute = fields[3].substring(11, 13).toInt()
        val second = fields[3].substring(13, 15).toInt()
        return BackupStatsModel(fields[2].toLong(), LocalDateTime.of(year, month, day, hour, minute, second))
    }

}