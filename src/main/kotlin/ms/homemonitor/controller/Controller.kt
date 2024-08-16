package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
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

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/energy")
    fun homeWizardEnergy(): HomeWizardEnergyData {
        return homeWizardDataProvider.getHomeWizardEnergyData()
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/water")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardDataProvider.getHomeWizardWaterData()
    }

    @Tag(name="Tado")
    @GetMapping("/tado")
    fun tado(): TadoResponseModel {
        return tadoDataProvider.getTadoResponse()
    }

    @Tag(name="Raspberry Pi")
    @GetMapping("/raspberrypi")
    fun raspberrypi(): RaspberryPiStatsModel {
        return raspberryPiStats.getRaspberryPiStats()
    }

    @Tag(name="Weerlive")
    @GetMapping("/weerlive")
    fun weerlive(): WeerLiveModel {
        return weerLive.getWeerLiveData()
    }

    @Tag(name="Eneco")
    @PostMapping("/eneco/recalculate")
    fun enecoRecalculate(): BigDecimal {
        return enecoService.recalculatingTotal()
    }

    @Tag(name="Eneco")
    @PostMapping("/eneco/data-source")
    fun enecoDataPost(@RequestBody source: String): List<EnecoDayConsumption> {
        return enecoService.updateEnecoStatistics(source)
    }

}