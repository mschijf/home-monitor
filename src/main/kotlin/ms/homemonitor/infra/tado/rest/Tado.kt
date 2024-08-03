package ms.homemonitor.infra.tado.rest

import com.sun.jdi.request.InvalidRequestStateException
import ms.homemonitor.config.TadoProperties
import ms.homemonitor.infra.tado.model.*
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange


// More information     : https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/
// About Tado and oAuth : https://support.tado.com/en/articles/8565472-how-do-i-update-my-rest-api-authentication-method-to-oauth-2
// About Tado and their api in general: https://github.com/kritsel/tado-openapispec-v2?tab=readme-ov-file

@Service
class Tado(private val tadoProperties: TadoProperties) {

    private val restTemplate = RestTemplate()

    private fun createAccessTokenRequest(): HttpEntity<MultiValueMap<String, String>> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = listOf( MediaType.APPLICATION_JSON)

        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("client_id", tadoProperties.clientId)
        bodyMap.add("client_secret", tadoProperties.clientSecret)
        bodyMap.add("username", tadoProperties.username)
        bodyMap.add("password", tadoProperties.password)
        bodyMap.add("grant_type", "password")

        val request = HttpEntity(bodyMap, headers)
        return request
    }

    private fun getAccessToken() : TadoOAuth {
        val tokenUrl = tadoProperties.tokenUrl
        val request = createAccessTokenRequest()
        val response = restTemplate.postForObject(tokenUrl, request, TadoOAuth::class.java)
            ?: throw InvalidRequestStateException("result is null while fetching access token")
        return response
    }

    private fun createAccessTokenHeaderRequest(accessToken: String): HttpEntity<Any?> {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        return HttpEntity<Any?>(headers)
    }

    private inline fun <reified T>getTadoResponse(endPoint: String, accessTokenHeaderRequest: HttpEntity<Any?>): ResponseEntity<T> {
        return restTemplate.exchange(
            url="${tadoProperties.baseRestUrl}$endPoint",
            method=HttpMethod.GET,
            requestEntity=accessTokenHeaderRequest,
            T::class.java
            )
    }


    private fun getTadoMe(accessTokenHeaderRequest: HttpEntity<Any?>) : TadoMe {
        return getTadoResponse<TadoMe> ("/me", accessTokenHeaderRequest)
            .body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado-me")
    }

    private fun getTadoZonesForHome(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int) : List<TadoZone> {
        return getTadoResponse<List<TadoZone>> (endPoint = "/homes/$homeId/zones", accessTokenHeaderRequest)
            .body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado Zones for home")
    }

    private fun getTadoStateForZone(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int, zoneId: Int) : TadoState {
        return getTadoResponse<TadoState> (endPoint = "/homes/$homeId/zones/$zoneId/state", accessTokenHeaderRequest)
            .body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado-state for zone $zoneId")
    }

    private fun getTadoOutsideWeather(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int): TadoWeather {
        return getTadoResponse<TadoWeather> (endPoint = "/homes/$homeId/weather", accessTokenHeaderRequest)
            .body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado-weather for home $homeId")
    }

    fun getTadoResponse(): TadoResponseModel {
        val tadoOAuth = getAccessToken()
        val request = createAccessTokenHeaderRequest(tadoOAuth.accessToken)
        val homeId = getTadoMe(request).homes[0].id
        val zoneId = getTadoZonesForHome(request, homeId)[0].id
        return TadoResponseModel(
            getTadoStateForZone(request, homeId, zoneId),
            getTadoOutsideWeather(request, homeId)
        )
    }
}