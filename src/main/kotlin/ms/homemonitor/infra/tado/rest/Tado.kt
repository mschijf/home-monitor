package ms.homemonitor.infra.tado.rest

import com.sun.jdi.request.InvalidRequestStateException
import ms.homemonitor.config.TadoProperties
import ms.homemonitor.infra.tado.model.*
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange


//More information: https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/

@Service
class Tado(private val tadoProperties: TadoProperties) {

    private val log = LoggerFactory.getLogger(Tado::class.java)
    private val restTemplate = RestTemplate()

    private fun getTadoOAuthEnvironment(): TadoOAuthEnvironment {
        // check the current env variables, including client_secret to use on https://my.tado.com/webapp/env.js

        val response = try {
            restTemplate.getForObject(tadoProperties.envUrl, String::class.java)
        } catch (ex: Exception) {
            log.warn("Error while reading environment from ${tadoProperties.envUrl}", ex)
            null
        }

        val result = if (response != null) {
            TadoOAuthEnvironment(
                clientId = response.substringAfter("clientId: '", missingDelimiterValue = "").substringBefore("'"),
                clientSecret = response.substringAfter("clientSecret: '", missingDelimiterValue = "").substringBefore("'"),
                baseUrl = response.substringAfter("baseUrl: '", missingDelimiterValue = "").substringBefore("'"),
            )
        } else {
            log.warn("Error while reading environment from ${tadoProperties.envUrl} --> reponse = null")
            null
        }

        if (result != null && result.isFilled()) {
            if (result.clientId != tadoProperties.clientId || result.clientSecret != tadoProperties.secret || result.baseUrl != tadoProperties.baseRestUrl) {
                log.warn("tado evironment doesn't match expected values!")
            }
            return result
        }

        log.warn("Error while reading environment from ${tadoProperties.envUrl} --> reponse = null, or some properties are not filled. Continue with default values from yaml file")
        return TadoOAuthEnvironment(
                clientId = tadoProperties.clientId,
                clientSecret = tadoProperties.secret,
                baseUrl = tadoProperties.baseRestUrl
            )
    }

    private fun createAccessTokenRequest(): HttpEntity<MultiValueMap<String, String>> {
        val tadoOAuthEnvironment = getTadoOAuthEnvironment()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = listOf( MediaType.APPLICATION_JSON)

        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("client_id", tadoOAuthEnvironment.clientId)
        bodyMap.add("client_secret", tadoOAuthEnvironment.clientSecret)
        bodyMap.add("username", tadoProperties.username)
        bodyMap.add("password", tadoProperties.password)
        bodyMap.add("grant_type", "password")

        val request = HttpEntity(bodyMap, headers)
        return request
    }


    private fun getAccessToken() : TadoOAuth {
        val endPoint = "https://auth.tado.com/oauth/token"
        val request = createAccessTokenRequest()
        val response = restTemplate.postForObject(endPoint, request, TadoOAuth::class.java)
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
            HttpMethod.GET,
            accessTokenHeaderRequest, T::class.java
            )
    }


    private fun getTadoMe(accessTokenHeaderRequest: HttpEntity<Any?>) : TadoMe {
        return getTadoResponse<TadoMe> ("api/v2/me", accessTokenHeaderRequest)
            .body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado-me")
    }

    private fun getTadoZonesForHome(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int) : List<TadoZone> {
        return getTadoResponse<List<TadoZone>> (
            endPoint = "api/v2/homes/$homeId/zones", accessTokenHeaderRequest)
            .body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado Zones for home")
    }

    private fun getTadoStateForZone(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int, zoneId: Int) : TadoState {
        return getTadoResponse<TadoState> (
            endPoint = "api/v2/homes/$homeId/zones/$zoneId/state", accessTokenHeaderRequest)
            .body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado-state for zone $zoneId")
    }

    private fun getTadoOutsideWeather(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int): TadoWeather {
        return getTadoResponse<TadoWeather> (
            endPoint = "api/v2/homes/$homeId/weather", accessTokenHeaderRequest)
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