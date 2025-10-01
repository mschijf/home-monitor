package ms.homemonitor.smartplug.restclient

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.rest.getForEntityWithHeader
import ms.homemonitor.smartplug.restclient.model.TuyaDataDetail
import ms.homemonitor.smartplug.restclient.model.TuyaDataResponse
import ms.homemonitor.smartplug.restclient.model.TuyaDeviceMasterData
import ms.homemonitor.smartplug.restclient.model.TuyaDeviceMasterDataResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * https://stackoverflow.com/questions/73874058/call-to-tuya-api-via-bash
 */

@Service
class TuyaClient(
    @param:Value("\${home-monitor.tuya.baseUrl}") private val baseUrl: String,
    private val tuyaBodyMap: TuyaBodyMap
) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(TuyaClient::class.java)

    fun getTuyaData(deviceId: String, startTime: LocalDateTime, endTime: LocalDateTime): List<TuyaDataDetail> {

        val zone = ZoneId.of("Europe/Berlin")
        val startTimeEpoch = startTime.toEpochSecond(zone.rules.getOffset(startTime))*1000
        val endTimeEpoch = endTime.toEpochSecond(zone.rules.getOffset(endTime))*1000

        val url="/v2.1/cloud/thing/$deviceId/report-logs?codes=add_ele&end_time=$endTimeEpoch&size=80&start_time=$startTimeEpoch"
        val bodyMap = tuyaBodyMap.getBodyMapForQuery(url)

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

    fun getTuyaDeviceMasterData(): List<TuyaDeviceMasterData> {

        val url="/v2.0/cloud/thing/device?page_size=20"
        val bodyMap = tuyaBodyMap.getBodyMapForQuery(url)

        try {

            val deviceUrl = baseUrl + url
            val dataResponse = restTemplate.getForEntityWithHeader<TuyaDeviceMasterDataResponse>(deviceUrl, HttpEntity(bodyMap))
            if (dataResponse.body?.success?:false) {
                val masterData = dataResponse.body?.result ?: throw Exception("no body or result from Tuya")
                return masterData
            } else {
                log.info("success is false for $url")
                return emptyList()
            }

        } catch (ex: Exception) {
            throw HomeMonitorException("Error getting tuya master data", ex)
        }
    }
}