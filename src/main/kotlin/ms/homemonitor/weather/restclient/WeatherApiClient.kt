package ms.homemonitor.weather.restclient

import ms.homemonitor.weather.restclient.model.WeatherApiCurrentData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject


@Service
class WeatherApiClient(
    @Value("\${home-monitor.weatherapi.baseRestUrl}") private val weatherapiBaseRestUrl: String,
    @Value("\${home-monitor.weatherapi.location}") private val location: String,
    @Value("\${home-monitor.weatherapi.apiKey}") private val apiKey: String,
    ) {

    private val restTemplate = RestTemplate()

    fun getCurrentWeather(): WeatherApiCurrentData {
        val response = restTemplate.getForObject<WeatherApiCurrentData>("$weatherapiBaseRestUrl/v1/current.json?q=$location&key=$apiKey")
        return response
    }
}


