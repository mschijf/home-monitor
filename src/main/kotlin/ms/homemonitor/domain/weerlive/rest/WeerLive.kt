package ms.homemonitor.domain.weerlive.rest

import ms.homemonitor.domain.weerlive.model.WeerLiveModel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WeerLive(
    @Value("\${weerlive.baseRestUrl}") private val baseRestUrl: String,
    @Value("\${weerlive.apiKey}") private val apiKey: String,
    @Value("\${weerlive.locationCoordinateN}") private val locationCoordinateN: Double,
    @Value("\${weerlive.locationCoordinateE}") private val locationCoordinateE: Double) {

    private val restTemplate = RestTemplate()
    private val log = LoggerFactory.getLogger(WeerLive::class.java)

    fun getWeerLiveData(): WeerLiveModel? {
        val uri = "${baseRestUrl}?" +
                "key=${apiKey}" +
                "&locatie=${locationCoordinateN},${locationCoordinateE}"

        try {
            val response = restTemplate
                .getForObject(
                    uri,
                    WeerLiveModel::class.java
                )
                ?: throw IllegalStateException("Could not get data from WeerLive. - response is null")
            return response
        } catch (e: Exception) {
            val response = restTemplate
                .getForObject(
                    uri,
                    String::class.java
                )
            log.error("Couldn't read $response. Ignoring input")
            return null
        }
    }

}
//https://weerlive.nl/api/weerlive_api_v2.php?key=5483fb4f12&locatie=52.0248,5.0918

