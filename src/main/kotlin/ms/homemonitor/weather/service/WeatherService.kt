package ms.homemonitor.weather.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.weather.repository.WeatherConditionRepository
import ms.homemonitor.weather.repository.WeatherRepository
import ms.homemonitor.weather.repository.model.WeatherConditionEntity
import ms.homemonitor.weather.repository.model.WeatherEntity
import ms.homemonitor.weather.restclient.WeatherApiClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class WeatherService(
    private val weatherClient: WeatherApiClient,
    private val weatherRepository: WeatherRepository,
    private val weatherConditionRepository: WeatherConditionRepository,
) {
    private val log = LoggerFactory.getLogger(WeatherService::class.java)
    private val zoneId = ZoneId.of("Europe/Berlin")

    fun processMeasurement() {
        try {
            val weatherResponse = weatherClient.getCurrentWeather()
            val conditionText = weatherResponse.current.condition.text.let {
                if (it.length > 64) {
                    log.warn("Current weather condition '$it' is too big, truncating")
                    it.take(64)
                } else {
                    it
                }
            }
            val condition = weatherConditionRepository.findByText(conditionText)
                ?: weatherConditionRepository.saveAndFlush(WeatherConditionEntity(text = conditionText))

            weatherRepository.saveAndFlush(
                WeatherEntity(
                    time = LocalDateTime.ofInstant(Instant.ofEpochMilli(weatherResponse.current.last_updated_epoch*1000), zoneId),
                    outsideTemperature = weatherResponse.current.temp_c,
                    humidityPercentage = weatherResponse.current.humidity,
                    condition = condition,
                    windKph = weatherResponse.current.wind_kph,
                    windDirection = weatherResponse.current.wind_dir,
                    pressureMb = weatherResponse.current.pressure_mb,
                    precipMm = weatherResponse.current.precip_mm,
                    cloud = weatherResponse.current.cloud,
                    uv = weatherResponse.current.uv
                )
            )
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Weather data", e)
        }
    }
}
