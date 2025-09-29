package ms.homemonitor.tuya.restclient

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.rest.getForEntityWithHeader
import ms.homemonitor.tuya.restclient.model.TuyaAuthResponse
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
class TuyaAccessToken(
    @Value("\${home-monitor.tuya.clientId}") private val clientId: String,
    @Value("\${home-monitor.tuya.secret}") private val secret: String,
    @Value("\${home-monitor.tuya.baseUrl}") private val baseUrl: String
) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(TuyaClient::class.java)

    fun getTuyaAccessToken(): String {

        val tuyaTime= Instant.now().epochSecond * 1000
        val url="/v1.0/token?grant_type=1"

        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("sign_method", "HMAC-SHA256")
        bodyMap.add("client_id", clientId)
        bodyMap.add("t", tuyaTime.toString())
        bodyMap.add("mode", "cors")
        bodyMap.add("Content-Type", "application/json")
        bodyMap.add("sign", getHmacSha256("${clientId}${tuyaTime}", HttpMethod.GET, url))

        try {
            val deviceUrl = baseUrl + url
            val deviceAuthorization = restTemplate.getForEntityWithHeader<TuyaAuthResponse>(deviceUrl, HttpEntity(bodyMap))
            return deviceAuthorization.body?.result?.accessToken?:throw Exception("empty body or result")
        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting access token", ex)
        }
    }

//    fun refreshTuyaAccessCode(refreshCode: String): String {
//        val tuyaTime= Instant.now().epochSecond * 1000
//        val url="/v1.0/token/$refreshCode"
//        val data = "${clientId}${tuyaTime}GET\n$emptyStringHmacSha256\n\n${url}"
//        val hashStringFinal = getHmacSha256(data)
//
//        try {
//            val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
//            bodyMap.add("sign_method", "HMAC-SHA256")
//            bodyMap.add("client_id", clientId)
//            bodyMap.add("t", tuyaTime.toString())
//            bodyMap.add("mode", "cors")
//            bodyMap.add("Content-Type", "application/json")
//            bodyMap.add("sign", hashStringFinal)
//
//            val deviceUrl = baseUrl + url
//            val deviceAuthorization = restTemplate.getForEntityWithHeader<TuyaAuthResponse>(deviceUrl, HttpEntity(bodyMap))
//
//            val x = deviceAuthorization.body
//
//            println("AFTER REFRESH")
//            println(x!!.toString())
//
//            return x!!.result!!.accessToken
//
//        } catch (ex: Exception) {
//            throw HomeMonitorException("Error getting access token", ex)
//        }
//    }

    fun getHmacSha256(preQualifierString: String, httpMethod: HttpMethod, url: String): String {
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