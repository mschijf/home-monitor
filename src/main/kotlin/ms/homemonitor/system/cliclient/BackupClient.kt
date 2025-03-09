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

    fun executeBackup(): BackupDataModel {
        val backupTime = LocalDateTime.now()
        try {
            log.debug("Starting backup")
            val backupOutput = commandExecutor.execCommand(backupScript)
            val backupOutputParts = backupOutput.first().trim().split("\\s+".toRegex())
            val backupFileName = backupOutputParts[0]
            val backupSize = backupOutputParts[1]
            log.info("Backup succeeded. Backup size: $backupSize kB. Backup file name: $backupFileName")
            return BackupDataModel(backupSize.toLong() * 1024, backupTime, backupFileName)
        } catch (e: Exception) {
            log.error("Backup failed, caused by ${e.message}")
            return BackupDataModel(-1, backupTime, "no-file")
        }
    }
}