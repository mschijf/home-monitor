package ms.homemonitor.infra.weerlive.rest

import ms.homemonitor.config.WeerLiveProperties
import ms.homemonitor.infra.weerlive.model.WeerLiveModel
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class WeerLive(
    private val weerLiveProperties: WeerLiveProperties) {

    private val restTemplate = RestTemplate()

    fun getWeerLiveData(): WeerLiveModel {
        val uri = "${weerLiveProperties.baseRestUrl}?" +
                "key=${weerLiveProperties.apiKey}" +
                "&locatie=${weerLiveProperties.locationCoordinateN},${weerLiveProperties.locationCoordinateE}"

        val response = restTemplate
            .getForObject(
                uri,
                WeerLiveModel::class.java)
            ?: throw IllegalStateException("Could not get data from WeerLive. - response is null")

        return response
    }

}
//https://weerlive.nl/api/weerlive_api_v2.php?key=5483fb4f12&locatie=52.0248,5.0918

