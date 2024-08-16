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
import ms.homemonitor.repository.EnecoDayConsumption
import ms.homemonitor.service.EnecoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class Controller(
    private val homeWizardDataProvider: HomeWizard,
    private val tadoDataProvider: Tado,
    private val raspberryPiStats: RaspberryPiStats,
    private val weerLive: WeerLive,
    private val enecoService: EnecoService
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

    @PostMapping("/eneco-recalculate")
    fun enecoRecalculate(): BigDecimal {
        return enecoService.recalculatingTotal()
    }

    @GetMapping("/eneco-data")
    fun enecoDataGet(): List<EnecoDayConsumption> {
        return enecoService.updateEnecoStatistics()
    }

    @PostMapping("/eneco-data-source")
    fun enecoDataPost(@RequestBody source: String): List<EnecoDayConsumption> {
        return enecoService.updateEnecoStatistics(source)
    }

    @PostMapping("/eneco-data-by-list")
    fun enecoDataPost(@RequestBody list: List<EnecoDayConsumption>): List<EnecoDayConsumption> {
        return enecoService.updateEnecoStatistics(list)
    }
}