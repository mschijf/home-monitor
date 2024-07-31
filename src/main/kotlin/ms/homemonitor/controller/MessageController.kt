package ms.homemonitor.controller

import ms.homemonitor.infra.homewizard.model.HomeWizardMeasurementData
import ms.homemonitor.infra.homewizard.rest.HomeWizard
import ms.homemonitor.infra.raspberrypi.RaspberryPiStats
import ms.homemonitor.infra.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.infra.tado.model.TadoResponseModel
import ms.homemonitor.infra.tado.rest.Tado
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val homeWizardDataProvider: HomeWizard,
    private val tadoDataProvider: Tado,
    private val raspberryPiStats: RaspberryPiStats
) {

    @GetMapping("/homewizard")
    fun homeWizard(): HomeWizardMeasurementData {
        return homeWizardDataProvider.getHomeWizardData()
    }

    @GetMapping("/tado")
    fun tado(): TadoResponseModel {
        return tadoDataProvider.getTadoResponse()
    }

    @GetMapping("/raspberrypi")
    fun raspberrypi(): RaspberryPiStatsModel {
        return raspberryPiStats.getRaspberryPiStats()
    }

}