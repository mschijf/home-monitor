package ms.homemonitor.system.cliclient

import ms.homemonitor.system.cliclient.model.BackupDataModel
import ms.homemonitor.shared.tools.commandline.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DropboxClient(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.system.dropbox_uploader}") private val dropboxUploader: String
) {

    private val dropboxRoot = "Backup/home-monitor/"
    private val log = LoggerFactory.getLogger(DropboxClient::class.java)

    fun getBackupStats(filter: String): List<BackupDataModel> {
        return try {
            commandExecutor.execCommand(dropboxUploader, arrayListOf("list", dropboxRoot))
                .drop(1)
                .filter { it.endsWith(filter) }
                .map { it.toBackupDataModel() }
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

    private fun String.toBackupDataModel() : BackupDataModel {
        val fields = this.split("\\s+".toRegex())
        val year = fields[3].substring(0, 4).toInt()
        val month = fields[3].substring(4, 6).toInt()
        val day = fields[3].substring(6, 8).toInt()
        val hour = fields[3].substring(9, 11).toInt()
        val minute = fields[3].substring(11, 13).toInt()
        val second = fields[3].substring(13, 15).toInt()
        return BackupDataModel(fields[2].toLong(), LocalDateTime.of(year, month, day, hour, minute, second), fields[3])
    }

    fun deleteFile(fileName: String) {
        try {
            commandExecutor.execCommand(dropboxUploader, arrayListOf("delete", "$dropboxRoot/$fileName"))
            log.info("Removing from dropbox $dropboxRoot/$fileName")
        } catch (e: Exception) {
            log.error("Couldn't remove file with filename $fileName, caused by ${e.message}")
        }
    }

}