package ms.homemonitor.domain.tado.rest

import ms.homemonitor.domain.tado.model.*
import ms.homemonitor.tools.getForEntityWithHeader
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
class Tado(
    private val tadoAccessToken: TadoAccessToken,
    @Value("\${tado.baseRestUrl}") private val baseRestUrl: String) {

    private val restTemplate = RestTemplate()

    private inline fun <reified T : Any>getTadoObjectViaRest(endPoint: String): T  {
        val headers = HttpHeaders()
        headers.setBearerAuth(tadoAccessToken.getTadoAccessToken())
        var response = restTemplate.getForEntityWithHeader<T>(endPoint, HttpEntity<Any?>(headers))
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            headers.setBearerAuth(tadoAccessToken.refreshedTadoAccessToken())
            response = restTemplate.getForEntityWithHeader<T>(endPoint, HttpEntity<Any?>(headers))
        }
        return response.body!!
    }

    private fun getTadoResponseAsStringViaRest(endPoint: String): String  {
        val headers = HttpHeaders()
        headers.setBearerAuth(tadoAccessToken.getTadoAccessToken())
        var response = restTemplate.getForEntityWithHeader<String>(endPoint, HttpEntity<Any?>(headers))
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            headers.setBearerAuth(tadoAccessToken.refreshedTadoAccessToken())
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
        return TadoResponseModel(getTadoStateForZone(homeId, zoneId), getTadoOutsideWeather(homeId))
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

