package ms.homemonitor.infra.raspberrypi

import ms.homemonitor.infra.raspberrypi.model.RaspberryPiStatsModel
import org.springframework.stereotype.Service

@Service
class RaspberryPiStats(private val commandExecutor: CommandExecutor) {

    fun getRaspberryPiStats(): RaspberryPiStatsModel {
        return RaspberryPiStatsModel(
            cpuTemperature = getCPUTemperature(),
            gpuTemperature = getGPUTemperature())
    }

    private fun getCPUTemperature(): Double {
        return try {
            commandExecutor
                .execCommand("cat", arrayListOf("/sys/class/thermal/thermal_zone0/temp"))[0].toDouble() / 1000.0
        } catch (e: Exception) {
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
            -1.0
        }
    }
}