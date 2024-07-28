package ms.homemonitor.homewizard.rest

import ms.homemonitor.config.HomeWizardProperties
import ms.homemonitor.homewizard.model.HomeWizardMeasurementData
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


