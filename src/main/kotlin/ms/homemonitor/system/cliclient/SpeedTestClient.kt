package ms.homemonitor.system.cliclient

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ms.homemonitor.shared.tools.commandline.CommandExecutor
import ms.homemonitor.system.cliclient.model.SpeedTestResultModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SpeedTestClient(
    private val commandExecutor: CommandExecutor,
    @Value("\${home-monitor.system.speedtest}") private val speedTestCmd: String
) {

    private val objectMapper = defineObjectMapper()

    fun getSpeedTestData(): SpeedTestResultModel {
        val response = commandExecutor.execCommand(speedTestCmd).first()
        return objectMapper.readValue<SpeedTestResultModel>(response)
    }

    private fun defineObjectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        return objectMapper
    }

}