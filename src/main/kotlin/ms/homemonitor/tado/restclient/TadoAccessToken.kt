package ms.homemonitor.tado.restclient

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.tado.repository.TadoTokenRepository
import ms.homemonitor.tado.repository.model.TadoTokenEntity
import ms.homemonitor.tado.restclient.model.TadoDeviceAuthorization
import ms.homemonitor.tado.restclient.model.TadoOAuth
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime


/**
 * See: https://support.tado.com/en/articles/8565472-how-do-i-authenticate-to-access-the-rest-api
 *
 * 1. execute via swagger: /admin/tado/getAccessDeviceUrl
 *    you get a response like
 *           {
 *             "device_code": "nC8RSDTEJf54nRqSFarTAmFj_cCma9CNC6eWiyszrNg",
 *             "expires_in": "300",
 *             "interval": 5,
 *             "user_code": "9PW4HG",
 *             "verification_uri": "https://login.tado.com/oauth2/device",
 *             "verification_uri_complete": "https://login.tado.com/oauth2/device?user_code=9PW4HG"
 *           }
 * 2. goto the url presented at 'verification_uri_complete' (so, in thi example: https://login.tado.com/oauth2/device?user_code=9PW4HG)
 * 3.    do submit the user_code, and login with tado credentials (in 1password)
 *
 * 4: final step: execute via swagger: /admin/tado/confirmDevice
 */

@Service
class TadoAccessToken(
    @Value("\${home-monitor.tado.deviceUrl}") private val deviceUrl: String,
    @Value("\${home-monitor.tado.tokenUrl}") private val tokenUrl: String,
    @Value("\${home-monitor.tado.clientId}") private val clientId: String,
    private val tadoTokenRepository: TadoTokenRepository
) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(TadoAccessToken::class.java)

    private var deviceAuthorization: TadoDeviceAuthorization? = null
    private var accessTokenObject: TadoOAuth? = null

    fun getTadoAccessToken(refresh: Boolean): String {
        try {
            if (refresh) {
                newTadoGetTokensUsingRefreshToken(accessTokenObject!!.refreshToken)
            }
            return getTadoOAuthObject().accessToken
        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting access token", ex)
        }
    }

    private fun newTadoGetTokensUsingRefreshToken(refreshToken: String): TadoOAuth? {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("client_id", clientId)
        bodyMap.add("grant_type", "refresh_token")
        bodyMap.add("refresh_token", refreshToken)

        resetAccessTokenObject(bodyMap)
        return accessTokenObject
    }

    private fun getTadoOAuthObject() : TadoOAuth {
        if (accessTokenObject == null) {
            val refreshToken = readLastRefreshToken()
            if (refreshToken.isEmpty()) {
                log.info("Refresh token is empty")
            }
            accessTokenObject = newTadoGetTokensUsingRefreshToken(refreshToken)
        }
        return accessTokenObject!!
    }

    fun newTadoAccessDeviceAuthorization(): Any? {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("client_id", clientId)
        bodyMap.add("scope", "offline_access")

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = listOf( MediaType.APPLICATION_JSON)

        deviceAuthorization = restTemplate.postForObject(deviceUrl, HttpEntity(bodyMap, headers), TadoDeviceAuthorization::class.java)

        return deviceAuthorization
    }

    fun confirmNewTadoAccessDeviceAuthorization(): TadoOAuth? {
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("client_id", clientId)
        bodyMap.add("device_code", deviceAuthorization!!.deviceCode)
        bodyMap.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code")

        resetAccessTokenObject(bodyMap)

        return accessTokenObject
    }

    private fun resetAccessTokenObject(bodyMap: MultiValueMap<String, String>) {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.accept = listOf( MediaType.APPLICATION_JSON)

        accessTokenObject = restTemplate.postForObject(tokenUrl, HttpEntity(bodyMap, headers), TadoOAuth::class.java)
        storeToken(accessTokenObject)
    }

    private fun storeToken(accessToken: TadoOAuth?) {
        if (accessToken != null) {
            tadoTokenRepository.deleteAll()
            tadoTokenRepository.save(
                TadoTokenEntity(LocalDateTime.now(), accessToken.refreshToken)
            )
        }
    }

    private fun readLastRefreshToken(): String {
        return tadoTokenRepository
            .readLast().firstOrNull()?.refreshToken ?: ""
    }

}