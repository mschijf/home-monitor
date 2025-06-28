package ms.homemonitor.shelly.restclient

import ms.homemonitor.shelly.restclient.model.ShellyThermometerData
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate


@Service
class ShellyClient(
    @Value("\${home-monitor.shelly.thermometerBaseRestUrl}") private val shellyBaseRestUrl: String,
    @Value("\${home-monitor.shelly.deviceId}") private val deviceId: String,
    @Value("\${home-monitor.shelly.authorizationKey}") private val authorizationKey:String
    ) {

    private val restTemplate = RestTemplate()

    fun getShellyThermometerData(): ShellyThermometerData {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("id", deviceId)
        bodyMap.add("auth_key", authorizationKey)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = listOf( MediaType.APPLICATION_JSON)

        val response = restTemplate
            .postForObject("${shellyBaseRestUrl}/status", HttpEntity(bodyMap, headers), ShellyThermometerData::class.java)
            ?: throw IllegalStateException("Could not get data from Shelly (Thermometer). - response is null")

        return response
    }
}


