package ms.homemonitor.system.cliclient

import ms.homemonitor.shared.tools.commandline.CommandExecutor
import ms.homemonitor.system.cliclient.model.PingDataModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PingClient(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.system.ping}") private val pingCmd: String
) {

    private val log = LoggerFactory.getLogger(PingClient::class.java)

    fun getPingData(): PingDataModel {
        val runTime = LocalDateTime.now()
        try {
            val pingOutput = commandExecutor.execCommand(pingCmd)
            return pingOutput.toPingDataModel(runTime)
        } catch (e: Exception) {
            log.error("Ping command failed, caused by ${e.message}")
            return emptyPingDataModel(runTime)
        }
    }

    private fun List<String>.toPingDataModel(runTime: LocalDateTime) : PingDataModel {
        val summary = this.takeLast(3)
        val rtt = summary[2].substringAfter("rtt min/avg/max/mdev = ").substringBefore(" ms").trim().split("/")
        return PingDataModel(
            runTime,
            summary[1].substringBefore("packets transmitted,").trim().toInt(),
            summary[1].substringAfter("packets transmitted,").substringBefore("received").trim().toInt(),
            rtt[0].toDouble(),
            rtt[1].toDouble(),
            rtt[2].toDouble(),
            rtt[3].toDouble(),
            host = summary[0].substringAfter("---").substringBefore("ping statistics").trim(),
        )
    }

    private fun emptyPingDataModel(runTime: LocalDateTime): PingDataModel {
        return PingDataModel(runTime,
            -1,
            -1,
            -1.0,
            -1.0,
            -1.0,
            0.0,
            "")
    }
}