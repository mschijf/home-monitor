package ms.homemonitor.infra.tado.rest

import com.sun.jdi.request.InvalidRequestStateException
import ms.homemonitor.config.TadoProperties
import ms.homemonitor.infra.tado.model.TadoOAuth
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class TadoAccessToken(
    private val tadoProperties: TadoProperties) {

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

    private fun getTadoOAuthObject() : TadoOAuth {
        val tokenUrl = tadoProperties.tokenUrl
        val request = createAccessTokenRequest()
        val response = restTemplate.postForObject(tokenUrl, request, TadoOAuth::class.java)
            ?: throw InvalidRequestStateException("result is null while fetching access token")
        return response
    }

    fun getTadoAccessToken(): String {
        return getTadoOAuthObject().accessToken
    }

}