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

        if (result != null && result.isFilled())
            return result


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
        val request = createAccessTokenRequest()
        val response = restTemplate.postForObject(tadoProperties.tokenUrl, request, TadoOAuth::class.java)
            ?: throw InvalidRequestStateException("result is null while fetching access token")
        return response
    }

    private fun createAccessTokenHeaderRequest(accessToken: String): HttpEntity<Any?> {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        return HttpEntity<Any?>(headers)
    }

    private fun getTadoMe(accessTokenHeaderRequest: HttpEntity<Any?>) : TadoMe {
        val response = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/me", HttpMethod.GET, accessTokenHeaderRequest, TadoMe::class.java)


        return response.body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado-me")
    }

    private fun getTadoZonesForHome(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int) : List<TadoZone> {
        val response = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/homes/$homeId/zones", HttpMethod.GET, accessTokenHeaderRequest, Array<TadoZone>::class.java)

        return response.body?.toList()
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado Zones for home")
    }

    private fun getTadoStateForZone(accessTokenHeaderRequest: HttpEntity<Any?>, homeId: Int, zoneId: Int) : TadoState {
        val response = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/homes/$homeId/zones/$zoneId/state", HttpMethod.GET, accessTokenHeaderRequest, TadoState::class.java)

        return response.body
            ?: throw InvalidRequestStateException("response-body is null while fetching Tado-state for zone $zoneId")
    }

//    fun getTadoJsonNodeOutput(accessTokenHeaderRequest: HttpEntity<Any?>): JsonNode {
//
//        val response2 : ResponseEntity<String> = restTemplate.exchange(
//            "${tadoProperties.baseRestUrl}api/v2/homes/1140394", HttpMethod.GET, accessTokenHeaderRequest, String::class.java)
//        val response3 : ResponseEntity<String> = restTemplate.exchange(
//            "${tadoProperties.baseRestUrl}api/v2/homes/1140394/zones", HttpMethod.GET, accessTokenHeaderRequest, String::class.java)
//        val response4 : ResponseEntity<String> = restTemplate.exchange(
//            "${tadoProperties.baseRestUrl}api/v2/homes/1140394/zones/1/dayReport?date=2024-07-27", HttpMethod.GET, accessTokenHeaderRequest, String::class.java)
//        val response5 : ResponseEntity<String> = restTemplate.exchange(
//            "${tadoProperties.baseRestUrl}api/v2/homes/1140394/zones/1/state", HttpMethod.GET, accessTokenHeaderRequest, String::class.java)
//
//
//        val root: JsonNode = ObjectMapper().readTree(response5.body)
//        return root
//    }
//
//    fun getTadoTmp():JsonNode {
//        val tadoOAuth = getAccessToken()
//        val request = createAccessTokenHeaderRequest(tadoOAuth.accessToken)
//        return getTadoJsonNodeOutput(request)
//    }

    fun getTadoStateData(): TadoState {
        val tadoOAuth = getAccessToken()
        val request = createAccessTokenHeaderRequest(tadoOAuth.accessToken)
        val homeId = getTadoMe(request).homes[0].id
        val zoneId = getTadoZonesForHome(request, homeId)[0].id
        return getTadoStateForZone(request, homeId, zoneId)
    }

}