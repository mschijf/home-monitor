package ms.homewizardapi.controller

import ms.homewizardapi.model.HomeWizardMeasurementData
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizardDataClient {
    private val baseUrl = "http://192.168.2.40"
    private val restTemplate = RestTemplate()

    fun getHomeWizardData(): HomeWizardMeasurementData {
        val response = restTemplate.getForObject("$baseUrl/api/v1/data", HomeWizardMeasurementData::class.java)
        //todo: check if we have a body/response --  error handling
        return response!!
    }
}


