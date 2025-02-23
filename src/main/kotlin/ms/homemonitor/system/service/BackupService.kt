package ms.homemonitor.system.service

import ms.homemonitor.shared.tools.commandline.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class BackupService(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.system.backup.script}") private val backupScript: String
) {

    private val log = LoggerFactory.getLogger(BackupService::class.java)

    fun executeBackup(keep: Int) {
        try {
            log.info("Starting backup")
            val backupOutput = commandExecutor.execCommand(backupScript, arrayListOf("_postgres", keep.toString()))
            val backupSize = backupOutput[1].split("\\s+".toRegex())[2]
            log.info("Backup succeeded. Backup size: $backupSize")
        } catch (e: Exception) {
            log.error("Backup failed, caused by ${e.message}")
        }
    }
}