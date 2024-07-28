package ms.homemonitor.controller

import ms.homemonitor.homewizard.model.HomeWizardMeasurementData
import ms.homemonitor.homewizard.rest.HomeWizard
import ms.homemonitor.tado.model.TadoState
import ms.homemonitor.tado.rest.Tado
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val homeWizardDataProvider: HomeWizard,
    private val tadoDataProvider: Tado) {

    @GetMapping("/homewizard")
    fun homeWizard(): HomeWizardMeasurementData {
        return homeWizardDataProvider.getHomeWizardData()
    }

    @GetMapping("/tado")
    fun tado(): TadoState {
        return tadoDataProvider.getTadoData()
    }
}