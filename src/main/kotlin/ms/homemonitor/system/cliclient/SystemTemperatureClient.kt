package ms.homemonitor.system.cliclient

import ms.homemonitor.system.cliclient.model.SystemTemperatureModel
import ms.homemonitor.shared.tools.commandline.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SystemTemperatureClient(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.system.cpuTemperatureCmd}") private val cpuTemperatureCmd: String,
    @Value("\${home-monitor.system.gpuTemperatureCmd}") private val gpuTemperatureCmd: String,
) {

    private val log = LoggerFactory.getLogger(SystemTemperatureClient::class.java)

    fun getSystemTemperature(): SystemTemperatureModel {
        return SystemTemperatureModel(
            cpuTemperature = getCPUTemperature(),
            gpuTemperature = getGPUTemperature()
        )
    }

    private fun getCPUTemperature(): Double {
        return try {
            commandExecutor
                .execCommand(cpuTemperatureCmd)
                .first()
                .toDouble() / 1000.0
        } catch (e: Exception) {
            log.error("Couldn't retrieve cpu temperature, caused by ${e.message}")
            -1.0
        }
    }

    private fun getGPUTemperature(): Double {
        return try {
            return commandExecutor.execCommand(gpuTemperatureCmd)
                .first()
                .substringAfter("temp=")
                .substringBefore("'C")
                .trim()
                .toDouble()
        } catch (e: Exception) {
            log.error("Couldn't retrieve gpu temperature, caused by ${e.message}")
            -1.0
        }
    }
}