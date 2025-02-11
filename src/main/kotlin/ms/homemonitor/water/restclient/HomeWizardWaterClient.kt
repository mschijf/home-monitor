package ms.homemonitor.water.restclient

import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizardWaterClient(
    @Value("\${home-monitor.homewizard.waterBaseRestUrl}") private val waterBaseRestUrl: String,
    ) {

    private val restTemplate = RestTemplate()

    fun getHomeWizardWaterData(): HomeWizardWaterData {
        val response = restTemplate
            .getForObject("${waterBaseRestUrl}/data", HomeWizardWaterData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard (Water). - response is null")

        return response
    }
}


