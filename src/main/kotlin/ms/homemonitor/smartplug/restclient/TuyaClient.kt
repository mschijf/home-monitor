package ms.homemonitor.smartplug.restclient

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.rest.getForEntityWithHeader
import ms.homemonitor.smartplug.restclient.model.TuyaDataDetail
import ms.homemonitor.smartplug.restclient.model.TuyaDataResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.time.*

/**
 * https://stackoverflow.com/questions/73874058/call-to-tuya-api-via-bash
 */

@Service
class TuyaClient(
    @Value("\${home-monitor.tuya.clientId}") private val clientId: String,
    @Value("\${home-monitor.tuya.baseUrl}") private val baseUrl: String,
    @Value("\${home-monitor.tuya.devices}") private val deviceList: List<String>,
    private val tuyaAccessToken: TuyaAccessToken
) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(TuyaClient::class.java)

    fun getTuyaData(deviceId: String, startTime: LocalDateTime, endTime: LocalDateTime): List<TuyaDataDetail> {

        val zone = ZoneId.of("Europe/Berlin")
        val zoneOffSet: ZoneOffset? = zone.rules.getOffset(LocalDateTime.now())

        val startTimeEpoch = startTime.toEpochSecond(zoneOffSet)*1000
        val endTimeEpoch = endTime.toEpochSecond(zoneOffSet)*1000

        val tuyaTime= Instant.now().epochSecond * 1000
        val accessToken = tuyaAccessToken.getTuyaAccessToken()
//        val url="/v2.1/cloud/thing/$deviceId/report-logs?codes=add_ele&end_time=1759010400000&size=80&start_time=1758924000000"
        val url="/v2.1/cloud/thing/$deviceId/report-logs?codes=add_ele&end_time=$endTimeEpoch&size=80&start_time=$startTimeEpoch"

        val bodyMap: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyMap.add("sign_method", "HMAC-SHA256")
        bodyMap.add("client_id", clientId)
        bodyMap.add("t", tuyaTime.toString())
        bodyMap.add("mode", "cors")
        bodyMap.add("Content-Type", "application/json")
        bodyMap.add("sign", tuyaAccessToken.getHmacSha256("${clientId}${accessToken}${tuyaTime}", HttpMethod.GET, url))
        bodyMap.add("access_token", accessToken)

        try {

            val deviceUrl = baseUrl + url
            val dataResponse = restTemplate.getForEntityWithHeader<TuyaDataResponse>(deviceUrl, HttpEntity(bodyMap))
            if (dataResponse.body?.success?:false) {
                val result = dataResponse.body?.result ?: throw Exception("no body or result from Tuya")
                val logs = result.logs?:emptyList()
                return logs.sortedBy { it.eventTime }
            } else {
                log.info("success is false for $url")
                return emptyList()
            }

        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting tuya data", ex)
        }
    }

    fun getDeviceList() = deviceList

}