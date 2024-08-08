package ms.homemonitor.controller

import ms.homemonitor.infra.homewizard.model.HomeWizardEnergyData
import ms.homemonitor.infra.homewizard.model.HomeWizardWaterData
import ms.homemonitor.infra.homewizard.rest.HomeWizard
import ms.homemonitor.infra.raspberrypi.RaspberryPiStats
import ms.homemonitor.infra.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.infra.tado.model.TadoResponseModel
import ms.homemonitor.infra.tado.rest.Tado
import ms.homemonitor.infra.weerlive.model.WeerLiveModel
import ms.homemonitor.infra.weerlive.rest.WeerLive
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val homeWizardDataProvider: HomeWizard,
    private val tadoDataProvider: Tado,
    private val raspberryPiStats: RaspberryPiStats,
    private val weerLive: WeerLive
) {

    @GetMapping("/homewizard_energy")
    fun homeWizardEnergy(): HomeWizardEnergyData {
        return homeWizardDataProvider.getHomeWizardEnergyData()
    }

    @GetMapping("/homewizard_water")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardDataProvider.getHomeWizardWaterData()
    }


    @GetMapping("/tado")
    fun tado(): TadoResponseModel {
        return tadoDataProvider.getTadoResponse()
    }

    @GetMapping("/raspberrypi")
    fun raspberrypi(): RaspberryPiStatsModel {
        return raspberryPiStats.getRaspberryPiStats()
    }

    @GetMapping("/weerlive")
    fun weerlive(): WeerLiveModel {
        return weerLive.getWeerLiveData()
    }


}