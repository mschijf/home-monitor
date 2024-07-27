package ms.homemonitoring.controller

import ms.homemonitoring.homewizard.model.HomeWizardMeasurementData
import ms.homemonitoring.homewizard.rest.HomeWizard
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val dataProvider: HomeWizard) {

    @GetMapping("/data")
    fun index(): HomeWizardMeasurementData {
        return dataProvider.getHomeWizardData()
    }

}