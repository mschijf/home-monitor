package ms.homemonitor.power.domain.rest

import ms.homemonitor.power.domain.model.HomeWizardEnergyData
import ms.homemonitor.water.domain.model.HomeWizardWaterData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizard(
    @Value("\${homewizard.energyBaseRestUrl}") private val energyBaseRestUrl: String,
    @Value("\${homewizard.waterBaseRestUrl}") private val waterBaseRestUrl: String,
    ) {

    private val restTemplate = RestTemplate()

    fun getHomeWizardEnergyData(): HomeWizardEnergyData {
        val response = restTemplate
            .getForObject("${energyBaseRestUrl}/data", HomeWizardEnergyData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard (Energy). - response is null")

        return response
    }

    fun getHomeWizardWaterData(): HomeWizardWaterData {
        val response = restTemplate
            .getForObject("${waterBaseRestUrl}/data", HomeWizardWaterData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard (Water). - response is null")

        return response
    }
}


