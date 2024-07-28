package ms.homemonitor.controller

import com.fasterxml.jackson.databind.JsonNode
import ms.homemonitor.homewizard.model.HomeWizardMeasurementData
import ms.homemonitor.homewizard.rest.HomeWizard
import ms.homemonitor.tado.rest.Tado
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val homeWizardDataProvider: HomeWizard,
    private val tadoDataProvider: Tado) {

    @GetMapping("/homewizard")
    fun homeWIzard(): HomeWizardMeasurementData {
        return homeWizardDataProvider.getHomeWizardData()
    }

    @GetMapping("/tado")
    fun tado(): JsonNode {
        return tadoDataProvider.getTadoData()
    }

}