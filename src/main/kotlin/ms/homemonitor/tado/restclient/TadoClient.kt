package ms.homemonitor.tado.restclient

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.shared.tools.rest.getForEntityWithHeader
import ms.homemonitor.tado.restclient.model.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate


// More information     : https://blog.scphillips.com/posts/2017/01/the-tado-api-v2/
// About Tado and oAuth : https://support.tado.com/en/articles/8565472-how-do-i-update-my-rest-api-authentication-method-to-oauth-2
// About Tado and their api in general: https://github.com/kritsel/tado-openapispec-v2?tab=readme-ov-file

@Service
class TadoClient(
    private val tadoAccessToken: TadoAccessToken,
    private val measurement: MicroMeterMeasurement,
    @Value("\${home-monitor.tado.baseRestUrl}") private val baseRestUrl: String) {

    private val restTemplate = RestTemplate()
    private var homeId: Int = -1
    private var zoneInfoCache = emptyMap<Int, TadoZone>()

    private val log = LoggerFactory.getLogger(javaClass)

    private inline fun <reified T : Any>getForEntityWithHeader(endPoint: String, httpEntity: HttpEntity<Any?>): ResponseEntity<T> {
        measurement.increaseCounter("tado.get")
        return restTemplate.getForEntityWithHeader<T>(endPoint, httpEntity)
    }

    private inline fun <reified T : Any>getTadoObjectViaRest(endPoint: String): T  {
        val headers = HttpHeaders()
        headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = false))
        var response = getForEntityWithHeader<T>(endPoint, HttpEntity<Any?>(headers))
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = true))
            response = getForEntityWithHeader<T>(endPoint, HttpEntity<Any?>(headers))
        }
        return response.body ?: throw HomeMonitorException(response.toString(), NullPointerException())
    }

    private fun getTadoResponseAsStringViaRest(endPoint: String): String  {
        val headers = HttpHeaders()
        headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = false))
        var response = getForEntityWithHeader<String>(endPoint, HttpEntity<Any?>(headers))
        if (response.statusCode == HttpStatus.UNAUTHORIZED) {
            headers.setBearerAuth(tadoAccessToken.getTadoAccessToken(refresh = true))
            response = getForEntityWithHeader<String>(endPoint, HttpEntity<Any?>(headers))
        }
        return response.body!!
    }

    private fun getTadoMe() : TadoMe {
        return getTadoObjectViaRest("${baseRestUrl}/me")
    }

    private fun getTadoZonesForHome(homeId: Int) : List<TadoZone> {
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/zones")
    }

    private fun getTadoOutsideWeather(homeId: Int): TadoWeather {
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/weather")
    }

    private fun getHomeId(): Int {
        if (homeId < 0) {
            homeId = getTadoMe().homes.first().id
            log.info("home id: $homeId")
        }
        return homeId
    }

    private fun getZoneId(homeId: Int): Int {
        zoneInfoCache = getTadoZonesForHome(homeId).associateBy { it.id }
        log.info("home id: $homeId")
        log.info("zoneInfo: $zoneInfoCache")
        return zoneInfoCache.keys.min()
    }

    fun getAllZones(): TadoZoneStates {
        val homeId = getHomeId()
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/zoneStates")
    }

    fun getTadoOutsideWeather(): TadoWeather {
        val homeId = getHomeId()
        return getTadoOutsideWeather(homeId)
    }

    fun getTadoZones(): List<TadoZone> {
        val homeId = getHomeId()
        return getTadoZonesForHome(homeId)
    }

    fun getTadoHistoricalInfo(day: LocalDate) : TadoDayReport {
        val homeId = getHomeId()
        val zoneId = getZoneId(homeId)
        return getTadoObjectViaRest("${baseRestUrl}/homes/$homeId/zones/$zoneId/dayReport?date=${day}")
    }

//    fun getTadoHistoricalInfoAsString(day: LocalDate) : String {
//        val homeId = getHomeId()
//        val zoneId = getZoneId(homeId)
//        return getTadoResponseAsStringViaRest("${baseRestUrl}/homes/$homeId/zones/$zoneId/dayReport?date=${day}")
//    }
}
