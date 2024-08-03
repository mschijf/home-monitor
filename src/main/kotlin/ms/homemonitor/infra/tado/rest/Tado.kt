package ms.homemonitor.infra.tado.rest

import ms.homemonitor.config.TadoProperties
import ms.homemonitor.infra.tado.model.*
import ms.homemonitor.infra.resttools.getForObjectWithHeader
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate



// More information     : https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/
// About Tado and oAuth : https://support.tado.com/en/articles/8565472-how-do-i-update-my-rest-api-authentication-method-to-oauth-2
// About Tado and their api in general: https://github.com/kritsel/tado-openapispec-v2?tab=readme-ov-file

@Service
class Tado(
    private val tadoAccessToken: TadoAccessToken,
    private val tadoProperties: TadoProperties) {

    private val restTemplate = RestTemplate()

    private fun createAccessTokenHeaderRequest(): HttpEntity<Any?> {
        val accessToken = tadoAccessToken.getTadoAccessToken()
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        return HttpEntity<Any?>(headers)
    }

    private fun getTadoMe(accessTokenHeaderRequest: HttpEntity<Any?>) : TadoMe {
        return restTemplate.getForObjectWithHeader<TadoMe> (
            "${tadoProperties.baseRestUrl}/me",
            accessTokenHeaderRequest)
    }

    private fun getTadoZonesForHome(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int) : List<TadoZone> {
        return restTemplate.getForObjectWithHeader<List<TadoZone>> (
                endPoint = "${tadoProperties.baseRestUrl}/homes/$homeId/zones",
                accessTokenHeaderRequest)
    }

    private fun getTadoStateForZone(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int, zoneId: Int) : TadoState {
        return restTemplate.getForObjectWithHeader<TadoState> (
            endPoint = "${tadoProperties.baseRestUrl}/homes/$homeId/zones/$zoneId/state",
            accessTokenHeaderRequest)
    }

    private fun getTadoOutsideWeather(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int): TadoWeather {
        return restTemplate.getForObjectWithHeader<TadoWeather> (
            endPoint = "${tadoProperties.baseRestUrl}/homes/$homeId/weather",
            accessTokenHeaderRequest)
    }

    fun getTadoResponse(): TadoResponseModel {
        val request = createAccessTokenHeaderRequest()
        val homeId = getTadoMe(request).homes[0].id
        val zoneId = getTadoZonesForHome(request, homeId)[0].id
        return TadoResponseModel(
            getTadoStateForZone(request, homeId, zoneId),
            getTadoOutsideWeather(request, homeId)
        )
    }
}