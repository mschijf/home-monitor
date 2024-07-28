package ms.homemonitor.tado.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import ms.homemonitor.config.TadoProperties
import ms.homemonitor.tado.model.TadoOAuth
import ms.homemonitor.tado.model.TadoOAuthEnvironment
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

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

    private fun getAccessToken() : TadoOAuth {
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

        val response = restTemplate.postForObject(tadoProperties.tokenUrl, request, TadoOAuth::class.java)

        return response!!
    }

    private fun getHomes(accessToken:String) :JsonNode {
        val restTemplate = RestTemplate()

        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)

        //https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/

        val response : ResponseEntity<String> = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/me", HttpMethod.GET, request, String::class.java)
        val response2 : ResponseEntity<String> = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/homes/1140394", HttpMethod.GET, request, String::class.java)
        val response3 : ResponseEntity<String> = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/homes/1140394/zones", HttpMethod.GET, request, String::class.java)
        val response4 : ResponseEntity<String> = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/homes/1140394/zones/1/dayReport?date=2024-07-27", HttpMethod.GET, request, String::class.java)
        val response5 : ResponseEntity<String> = restTemplate.exchange(
            "${tadoProperties.baseRestUrl}api/v2/homes/1140394/zones/1/state", HttpMethod.GET, request, String::class.java)


        val root: JsonNode = ObjectMapper().readTree(response4.body)

        return root
    }

    fun getTadoData():JsonNode {
        //home
        //zones
        //state
        val tadoOAuth = getAccessToken()
        return getHomes(tadoOAuth.accessToken)
    }

}