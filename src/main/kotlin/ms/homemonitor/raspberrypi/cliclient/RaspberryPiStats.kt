package ms.homemonitor.raspberrypi.cliclient

import ms.homemonitor.raspberrypi.cliclient.model.RaspberryPiStatsModel
import ms.homemonitor.shared.tools.commandline.CommandExecutor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RaspberryPiStats(private val commandExecutor: CommandExecutor) {

    private val log = LoggerFactory.getLogger(RaspberryPiStats::class.java)

    fun getRaspberryPiStats(): RaspberryPiStatsModel {
        return RaspberryPiStatsModel(
            cpuTemperature = getCPUTemperature(),
            gpuTemperature = getGPUTemperature())
    }

    private fun getCPUTemperature(): Double {
        return try {
            commandExecutor
                .execCommand("cat", arrayListOf("/sys/class/thermal/thermal_zone0/temp"))
                .first()
                .toDouble() / 1000.0
        } catch (e: Exception) {
            log.error("Couldn't retrieve cpu temperature, caused by ${e.message}")
            -1.0
        }
    }

    private fun getGPUTemperature(): Double {
        return try {
            return commandExecutor.execCommand("vcgencmd", arrayListOf("measure_temp"))[0]
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