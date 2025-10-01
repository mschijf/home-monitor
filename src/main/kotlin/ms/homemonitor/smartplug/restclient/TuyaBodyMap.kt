package ms.homemonitor.smartplug.restclient

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.TimedCache
import ms.homemonitor.shared.tools.rest.getForEntityWithHeader
import ms.homemonitor.smartplug.restclient.model.TuyaAuthResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * https://stackoverflow.com/questions/73874058/call-to-tuya-api-via-bash
 */

@Service
class TuyaBodyMap(
    @param:Value("\${home-monitor.tuya.clientId}") private val clientId: String,
    @param:Value("\${home-monitor.tuya.secret}") private val secret: String,
    @param:Value("\${home-monitor.tuya.baseUrl}") private val baseUrl: String
) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(TuyaClient::class.java)
    private val cachedAccessToken = TimedCache<String>()


    fun getBodyMapForQuery(url: String): MultiValueMap<String, String> {
        return getBody(url, useAccessToken = true)
    }

    private fun getBody(url: String, useAccessToken: Boolean): MultiValueMap<String, String> {
        val tuyaTime= Instant.now().epochSecond * 1000
        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("sign_method", "HMAC-SHA256")
        bodyMap.add("client_id", clientId)
        bodyMap.add("t", tuyaTime.toString())
        bodyMap.add("mode", "cors")
        bodyMap.add("Content-Type", "application/json")
        if (useAccessToken) {
            val accessToken = getTuyaAccessToken()
            bodyMap.add("sign", getHmacSha256("${clientId}${accessToken}${tuyaTime}", HttpMethod.GET, url))
            bodyMap.add("access_token", accessToken)
        } else {
            bodyMap.add("sign", getHmacSha256("${clientId}${tuyaTime}", HttpMethod.GET, url))
        }
        return bodyMap
    }

    private fun getTuyaAccessToken(): String {
        val accessToken = cachedAccessToken.get()
        if (accessToken != null)
            return accessToken

        val url="/v1.0/token?grant_type=1"
        val bodyMap = getBody(url, useAccessToken = false)

        try {
            val deviceUrl = baseUrl + url
            val response = restTemplate.getForEntityWithHeader<TuyaAuthResponse>(deviceUrl, HttpEntity(bodyMap))
            val authenticationDetails = response.body?.result?:throw Exception("empty body or result")
            cachedAccessToken.put(authenticationDetails.accessToken, authenticationDetails.expireTime-10)
            return authenticationDetails.accessToken
        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting access token", ex)
        }
    }

    private fun getHmacSha256(preQualifierString: String, httpMethod: HttpMethod, url: String): String {
        val emptyStringHmacSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

        val inputData = "$preQualifierString$httpMethod\n$emptyStringHmacSha256\n\n$url"
        val hmacSHA256 = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        hmacSHA256.init(secretKeySpec)
        val hash = hmacSHA256.doFinal(inputData.toByteArray())
        val hashString = hash.joinToString("") { String.format("%02x", it) }
        return hashString.uppercase()
    }

}