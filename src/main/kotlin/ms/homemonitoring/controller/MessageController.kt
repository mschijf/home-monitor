package ms.homemonitoring.controller

import com.fasterxml.jackson.databind.JsonNode
import ms.homemonitoring.homewizard.model.HomeWizardMeasurementData
import ms.homemonitoring.homewizard.rest.HomeWizard
import ms.homemonitoring.tado.model.TadoOAuth
import ms.homemonitoring.tado.rest.Tado
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