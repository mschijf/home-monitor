package ms.homemonitor.domain.homewizard.rest

import ms.homemonitor.domain.homewizard.HomeWizardProperties
import ms.homemonitor.domain.homewizard.model.HomeWizardEnergyData
import ms.homemonitor.domain.homewizard.model.HomeWizardWaterData
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class HomeWizard(
    private val homeWizardProperties: HomeWizardProperties
) {

    private val restTemplate = RestTemplate()

    fun getHomeWizardEnergyData(): HomeWizardEnergyData {
        val response = restTemplate
            .getForObject("${homeWizardProperties.energyBaseRestUrl}/data", HomeWizardEnergyData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard (Energy). - response is null")

        return response
    }

    fun getHomeWizardWaterData(): HomeWizardWaterData {
        val response = restTemplate
            .getForObject("${homeWizardProperties.waterBaseRestUrl}/data", HomeWizardWaterData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard (Water). - response is null")

        return response
    }
}


