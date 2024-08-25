package ms.homemonitor.infra.tado.rest

import ms.homemonitor.config.TadoProperties
import ms.homemonitor.infra.tado.model.TadoOAuth
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class TadoAccessToken(
    private val tadoProperties: TadoProperties) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(TadoAccessToken::class.java)

    private var accessTokenObject: TadoOAuth? = null

    private fun getUsernamePassword(): Pair<String, String> {
        return Pair(tadoProperties.username, tadoProperties.password)
    }

    private fun getBodyMapUsingPasswordGrant(): MultiValueMap<String, String> {
        val (username, password) = getUsernamePassword()

        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("client_id", tadoProperties.clientId)
        bodyMap.add("client_secret", tadoProperties.clientSecret)
        bodyMap.add("username", username)
        bodyMap.add("password", password)
        bodyMap.add("grant_type", "password")
        return bodyMap
    }

    private fun getBodyMapUsingRefreshTokenGrant(): MultiValueMap<String, String> {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("client_id", tadoProperties.clientId)
        bodyMap.add("client_secret", tadoProperties.clientSecret)
        bodyMap.add("grant_type", "refresh_token")
        bodyMap.add("refresh_token", accessTokenObject!!.refreshToken)
        return bodyMap
    }

    private fun refreshAccessTokenObject() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = listOf( MediaType.APPLICATION_JSON)

        val bodyMap = if (accessTokenObject == null) {
            log.info("Refreshing access token - use username and password")
            getBodyMapUsingPasswordGrant()
        } else {
            log.debug("Refreshing access token - use refresh token")
            getBodyMapUsingRefreshTokenGrant()
        }

        try {
            accessTokenObject = restTemplate.postForObject(tadoProperties.tokenUrl, HttpEntity(bodyMap, headers), TadoOAuth::class.java)
        } catch (e: HttpClientErrorException) {
            log.info("Refreshing with refresh token did not work. Get access token - use username and password")
            val newBodyMap = getBodyMapUsingPasswordGrant()
            accessTokenObject = restTemplate.postForObject(tadoProperties.tokenUrl, HttpEntity(newBodyMap, headers), TadoOAuth::class.java)
        }
    }

    private fun getTadoOAuthObject() : TadoOAuth {
        if (accessTokenObject == null) {
            refreshAccessTokenObject()
        }
        return accessTokenObject!!
    }

    fun getTadoAccessToken(): String {
        return getTadoOAuthObject().accessToken
    }

    fun refreshedTadoAccessToken(): String {
        refreshAccessTokenObject()
        return getTadoOAuthObject().accessToken
    }

}