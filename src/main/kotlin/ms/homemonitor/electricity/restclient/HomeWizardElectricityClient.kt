package ms.homemonitor.electricity.restclient

import ms.homemonitor.electricity.restclient.model.HomeWizardElectricityData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizardElectricityClient(
    @Value("\${home-monitor.homewizard.electricityBaseRestUrl}") private val electricityBaseRestUrl: String,
    ) {

    private val restTemplate = RestTemplate()

    fun getHomeWizardElectricityData(): HomeWizardElectricityData {
        val response = restTemplate
            .getForObject("${electricityBaseRestUrl}/data", HomeWizardElectricityData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard (Electricity). - response is null")

        return response
    }
}


