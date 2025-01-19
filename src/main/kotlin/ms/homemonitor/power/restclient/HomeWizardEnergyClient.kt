package ms.homemonitor.power.restclient

import ms.homemonitor.power.restclient.model.HomeWizardEnergyData
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizardEnergyClient(
    @Value("\${homewizard.energyBaseRestUrl}") private val energyBaseRestUrl: String,
    ) {

    private val restTemplate = RestTemplate()

    fun getHomeWizardEnergyData(): HomeWizardEnergyData {
        val response = restTemplate
            .getForObject("${energyBaseRestUrl}/data", HomeWizardEnergyData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard (Energy). - response is null")

        return response
    }
}


