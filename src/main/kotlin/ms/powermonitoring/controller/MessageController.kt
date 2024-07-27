package ms.powermonitoring.controller

import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementDataTimed
import ms.powermonitoring.homewizard.rest.HomeWizard
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val dataProvider: HomeWizard) {

    @GetMapping("/data")
    fun index(): HomeWizardMeasurementDataTimed {
        return dataProvider.getHomeWizardData()
    }

}