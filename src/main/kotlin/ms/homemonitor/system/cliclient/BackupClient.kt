package ms.homemonitor.system.cliclient

import ms.homemonitor.shared.tools.commandline.CommandExecutor
import ms.homemonitor.system.cliclient.model.BackupDataModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BackupClient(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.system.backup.script}") private val backupScript: String
) {

    private val log = LoggerFactory.getLogger(BackupClient::class.java)

    fun executeBackup(keep: Int): BackupDataModel {
        val backupTime = LocalDateTime.now()
        try {
            log.info("Starting backup")
            val backupOutput = commandExecutor.execCommand(backupScript, arrayListOf("_postgres", keep.toString()))
            val backupSize = backupOutput[1].split("\\s+".toRegex())[2].trim()
            val backupFileName = backupOutput[2].trim().split("\\s+".toRegex())[4]
            log.info("Backup succeeded. Backup size: $backupSize kB. Backup file name: $backupFileName")
            return BackupDataModel(backupSize.toLong() * 1024, backupTime )
        } catch (e: Exception) {
            log.error("Backup failed, caused by ${e.message}")
            return BackupDataModel(-1, backupTime)
        }
    }
}