package ms.homemonitor.system.cliclient

import ms.homemonitor.system.cliclient.model.SystemTemperatureModel
import ms.homemonitor.shared.tools.commandline.CommandExecutor
import ms.homemonitor.system.cliclient.model.BackupResultModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SystemClient(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.system.cpuTemperatureFile}") private val cpuTemperatureFile: String,
    @Value("\${home-monitor.system.gpuTemperatureCmd}") private val gpuTemperatureCmd: String,
    @Value("\${home-monitor.system.backup.script}") private val backupScript: String
) {

    private val log = LoggerFactory.getLogger(SystemClient::class.java)

    fun getSystemTemperature(): SystemTemperatureModel {
        return SystemTemperatureModel(
            cpuTemperature = getCPUTemperature(),
            gpuTemperature = getGPUTemperature()
        )
    }

    private fun getCPUTemperature(): Double {
        return try {
            commandExecutor
                .execCommand("cat", arrayListOf(cpuTemperatureFile))
                .first()
                .toDouble() / 1000.0
        } catch (e: Exception) {
            log.error("Couldn't retrieve cpu temperature, caused by ${e.message}")
            -1.0
        }
    }

    private fun getGPUTemperature(): Double {
        return try {
            return commandExecutor.execCommand(gpuTemperatureCmd, arrayListOf("measure_temp"))[0]
                .substringAfter("temp=")
                .substringBefore("'C")
                .trim()
                .toDouble()
        } catch (e: Exception) {
            log.error("Couldn't retrieve gpu temperature, caused by ${e.message}")
            -1.0
        }
    }

    fun executeBackup(keep: Int): BackupResultModel {
        try {
            log.info("Starting backup")
            val backupOutput = commandExecutor.execCommand(backupScript, arrayListOf("_postgres", keep.toString()))
            val backupSize = backupOutput[1].split("\\s+".toRegex())[2].trim()
            log.info("Backup succeeded. Backup size: $backupSize")
            return BackupResultModel(backupSize.toLong())
        } catch (e: Exception) {
            log.error("Backup failed, caused by ${e.message}")
            return BackupResultModel(-1)
        }
    }

}