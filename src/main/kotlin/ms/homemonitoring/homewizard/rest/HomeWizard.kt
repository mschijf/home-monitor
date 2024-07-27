package ms.homemonitoring.homewizard.rest

import ms.homemonitoring.config.HomeWizardProperties
import ms.homemonitoring.homewizard.model.HomeWizardMeasurementData
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizard(
    private val homeWizardProperties: HomeWizardProperties) {

    private val restTemplate = RestTemplate()

    fun getHomeWizardData(): HomeWizardMeasurementData {
        val response = restTemplate
            .getForObject("${homeWizardProperties.url}/api/v1/data", HomeWizardMeasurementData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard. - response is null")

        return response
    }

}


