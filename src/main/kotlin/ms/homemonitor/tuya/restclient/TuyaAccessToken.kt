package ms.homemonitor.tuya.restclient

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.rest.getForEntityWithHeader
import ms.homemonitor.tuya.restclient.model.TuyaAuthResponse
import ms.homemonitor.tuya.restclient.model.TuyaDataResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * https://stackoverflow.com/questions/73874058/call-to-tuya-api-via-bash
 */

private const val emptyStringHmacSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
@Service
class TuyaAccessToken(
    @Value("\${home-monitor.tuya.clientId}") private val clientId: String,
    @Value("\${home-monitor.tuya.secret}") private val secret: String,
    @Value("\${home-monitor.tuya.baseUrl}") private val baseUrl: String
) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(TuyaAccessToken::class.java)

    private val deviceId = "bf60ff004a1abac9896jej"

    fun getTuyaAccessToken(): String {

        val tuyaTime= Instant.now().epochSecond * 1000
        val url="/v1.0/token?grant_type=1"
        val data = "${clientId}${tuyaTime}GET\n$emptyStringHmacSha256\n\n${url}"
        val hashStringFinal = getHmacSha256(data)

        try {
            val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
            bodyMap.add("sign_method", "HMAC-SHA256")
            bodyMap.add("client_id", clientId)
            bodyMap.add("t", tuyaTime.toString())
            bodyMap.add("mode", "cors")
            bodyMap.add("Content-Type", "application/json")
            bodyMap.add("sign", hashStringFinal)

            val deviceUrl = baseUrl + url
            val deviceAuthorization = restTemplate.getForEntityWithHeader<TuyaAuthResponse>(deviceUrl, HttpEntity(bodyMap))
            val x = deviceAuthorization.body
            println("========================= ACCESSTOKEN")
            println(deviceAuthorization)
            println("========================= ACCESSTOKEN")
            return x!!.result!!.accessToken

        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting access token", ex)
        }
    }

    fun refreshTuyaAccessCode(refreshCode: String): String {
        val tuyaTime= Instant.now().epochSecond * 1000
        val url="/v1.0/token/$refreshCode"
        val data = "${clientId}${tuyaTime}GET\n$emptyStringHmacSha256\n\n${url}"
        val hashStringFinal = getHmacSha256(data)

        try {
            val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
            bodyMap.add("sign_method", "HMAC-SHA256")
            bodyMap.add("client_id", clientId)
            bodyMap.add("t", tuyaTime.toString())
            bodyMap.add("mode", "cors")
            bodyMap.add("Content-Type", "application/json")
            bodyMap.add("sign", hashStringFinal)

            val deviceUrl = baseUrl + url
            val deviceAuthorization = restTemplate.getForEntityWithHeader<TuyaAuthResponse>(deviceUrl, HttpEntity(bodyMap))

            val x = deviceAuthorization.body

            println("AFTER REFRESH")
            println(x!!.toString())

            return x!!.result!!.accessToken

        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting access token", ex)
        }
    }


    fun getTuyaData(deviceId: String, startTime: LocalDateTime, endTime: LocalDateTime): String {
        val tuyaTime= Instant.now().epochSecond * 1000
        val accessToken = getTuyaAccessToken()
        val url="/v2.1/cloud/thing/$deviceId/report-logs?codes=add_ele&end_time=1759010400000&size=80&start_time=1758924000000"
//        val url="/v2.1/cloud/thing/$deviceID/report-logs?codes=add_ele&start_time=1758924000000&end_time=1759010400000&size=80"
        val data="${clientId}${accessToken}${tuyaTime}GET\n$emptyStringHmacSha256\n\n${url}"

        val hashStringFinal = getHmacSha256(data)

        try {
            val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
            bodyMap.add("sign_method", "HMAC-SHA256")
            bodyMap.add("client_id", clientId)
            bodyMap.add("t", tuyaTime.toString())
            bodyMap.add("mode", "cors")
            bodyMap.add("Content-Type", "application/json")
            bodyMap.add("sign", hashStringFinal)
            bodyMap.add("access_token", accessToken)

            val deviceUrl = baseUrl + url
            val dataResponse = restTemplate.getForEntityWithHeader<TuyaDataResponse>(deviceUrl, HttpEntity(bodyMap))
            println("========================= DATA")
            println(dataResponse)
            println("========================= DATA")
            val x = dataResponse.body
            val logs =  x!!.result!!.logs

            logs.sortedBy { it.eventTime }.forEach {
                println("${ Instant.ofEpochMilli(it.eventTime) }  --> ${it.value}")
            }

            return "Logs:  Aantal:${logs.size}, Som: ${logs.sumOf { it.value }.toString()}"

        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting tuya data", ex)
        }

    }

    fun getTuyaDataAllDevices():String {
        return getTuyaData(
            deviceId,
            LocalDate.now().minusDays(1).atStartOfDay(),
            LocalDate.now().atStartOfDay())
    }


    private fun getHmacSha256(inputData: String): String {
        val hmacSHA256 = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        hmacSHA256.init(secretKeySpec)
        val hash = hmacSHA256.doFinal(inputData.toByteArray())
        val hashString = hash.joinToString("") { String.format("%02x", it) }
        return hashString.uppercase()
    }

}