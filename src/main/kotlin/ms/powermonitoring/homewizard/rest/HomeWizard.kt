package ms.powermonitoring.homewizard.rest

import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizard {
    private val baseUrl = "http://192.168.2.40"
    private val restTemplate = RestTemplate()

    fun getHomeWizardData(): HomeWizardMeasurementData {
        val response = restTemplate
            .getForObject("$baseUrl/api/v1/data", HomeWizardMeasurementData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard. - response is null")

        return response
    }

}


