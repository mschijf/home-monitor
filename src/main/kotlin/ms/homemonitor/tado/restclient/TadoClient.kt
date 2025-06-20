package ms.homemonitor.tado.restclient

import ms.homemonitor.tado.restclient.model.TadoDayReport
import ms.homemonitor.tado.restclient.model.TadoMe
import ms.homemonitor.tado.restclient.model.TadoResponseModel
import ms.homemonitor.tado.restclient.model.TadoState
import ms.homemonitor.tado.restclient.model.TadoWeather
import ms.homemonitor.tado.restclient.model.TadoZone
import ms.homemonitor.shared.tools.rest.getForEntityWithHeader
import ms.homemonitor.tado.restclient.model.TadoDevice
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate


// More information     : https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/
// About Tado and oAuth : https://support.tado.com/en/articles/8565472-how-do-i-update-my-rest-api-authentication-method-to-oauth-2
// About Tado and their api in general: https://github.com/kritsel/tado-openapispec-v2?tab=readme-ov-file

@Service
class TadoClient(
    private val tadoAccessToken: TadoAccessToken,
    @Value("\${home-monitor.tado.baseRestUrl}") private val baseRestUrl: String) {

    private val restTemplate = RestTemplate()

    private inline fun <reified T : Any>getTadoObjectViaRest(endPoint: String): T  {
        val headers = HttpHeaders()
        headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = false))
        var response = restTemplate.getForEntityWithHeader<T>(endPoint, HttpEntity<Any?>(headers))
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = true))
            response = restTemplate.getForEntityWithHeader<T>(endPoint, HttpEntity<Any?>(headers))
        }
        return response.body!!
    }

    private fun getTadoResponseAsStringViaRest(endPoint: String): String  {
        val headers = HttpHeaders()
        headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = false))
        var response = restTemplate.getForEntityWithHeader<String>(endPoint, HttpEntity<Any?>(headers))
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = true))
            response = restTemplate.getForEntityWithHeader<String>(endPoint, HttpEntity<Any?>(headers))
        }
        return response.body!!
    }

    private fun getTadoMe() : TadoMe {
        return getTadoObjectViaRest("${baseRestUrl}/me")
    }

    private fun getTadoZonesForHome(homeId: Int) : List<TadoZone> {
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/zones")
    }

    private fun getTadoStateForZone(homeId: Int, zoneId: Int) : TadoState {
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/zones/$zoneId/state")
    }

    private fun getTadoOutsideWeather(homeId: Int): TadoWeather {
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/weather")
    }

    fun getTadoResponse(): TadoResponseModel {
        val homeId = getTadoMe().homes[0].id
        val zoneId = getTadoZonesForHome(homeId)[0].id
        return TadoResponseModel(
            getTadoStateForZone(homeId, zoneId),
            getTadoOutsideWeather(homeId))
    }

    fun getTadoDeviceInfo(): TadoDevice {
        val homeId = getTadoMe().homes[0].id
        val deviceList: List<TadoDevice> = getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/devices")
        return deviceList.first { it.deviceType == "RU02" }
    }

    fun getTadoHistoricalInfo(day: LocalDate) : TadoDayReport {
        val homeId = getTadoMe().homes[0].id
        val zoneId = getTadoZonesForHome(homeId)[0].id
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/zones/$zoneId/dayReport?date=${day}")
    }

    fun getTadoHistoricalInfoAsString(day: LocalDate) : String {
        val homeId = getTadoMe().homes[0].id
        val zoneId = getTadoZonesForHome(homeId)[0].id
        return getTadoResponseAsStringViaRest("${baseRestUrl}/homes/$homeId/zones/$zoneId/dayReport?date=${day}")
    }


}

