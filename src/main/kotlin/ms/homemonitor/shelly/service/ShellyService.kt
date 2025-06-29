package ms.homemonitor.shelly.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.utcTimeToLocalTime
import ms.homemonitor.shelly.repository.ShellyRepository
import ms.homemonitor.shelly.repository.model.ShellyEntity
import ms.homemonitor.shelly.restclient.ShellyClient
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ShellyService(
    private val shellyClient: ShellyClient,
    private val shellyRepository: ShellyRepository,
) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun processMeasurement() {
        try {
            val now = LocalDateTime.now()
            val shellyResponse = shellyClient.getShellyThermometerData()
            shellyRepository.saveAndFlush(
                ShellyEntity(
                    time = now,
                    insideTemperature = shellyResponse.data.deviceStatus.temperature.value,
                    humidityPercentage = shellyResponse.data.deviceStatus.humidity.value,
                    updated = LocalDateTime
                        .parse(shellyResponse.data.deviceStatus.updated, formatter)
                        .utcTimeToLocalTime()
                )
            )
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Shelly data", e)
        }
    }
}