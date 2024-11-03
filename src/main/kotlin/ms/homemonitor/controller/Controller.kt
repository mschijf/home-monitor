package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.domain.eneco.EnecoUpdateService
import ms.homemonitor.domain.eneco.model.EnecoConsumption
import ms.homemonitor.domain.homewizard.model.HomeWizardEnergyData
import ms.homemonitor.domain.homewizard.model.HomeWizardWaterData
import ms.homemonitor.domain.homewizard.rest.HomeWizard
import ms.homemonitor.domain.log.LogService
import ms.homemonitor.domain.log.model.LogLine
import ms.homemonitor.domain.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.domain.raspberrypi.rest.RaspberryPiStats
import ms.homemonitor.domain.tado.model.TadoResponseModel
import ms.homemonitor.domain.tado.rest.Tado
import ms.homemonitor.domain.weerlive.model.WeerLiveModel
import ms.homemonitor.domain.weerlive.rest.WeerLive
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller(
    private val homeWizardDataProvider: HomeWizard,
    private val tadoDataProvider: Tado,
    private val raspberryPiStats: RaspberryPiStats,
    private val weerLive: WeerLive,
    private val enecoUpdateService: EnecoUpdateService,
    private val logService: LogService
) {

    private val log = LoggerFactory.getLogger(Controller::class.java)

    @Tag(name="Admin")
    @GetMapping("/ping")
    fun ping() {
        return
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/energy/current")
    fun homeWizardEnergy(): HomeWizardEnergyData {
        return homeWizardDataProvider.getHomeWizardEnergyData()
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/water/current")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardDataProvider.getHomeWizardWaterData()
    }

    @Tag(name="Tado")
    @GetMapping("/tado/current")
    fun tado(): TadoResponseModel {
        return tadoDataProvider.getTadoResponse()
    }

    @Tag(name="Raspberry Pi")
    @GetMapping("/raspberrypi/current")
    fun raspberrypi(): RaspberryPiStatsModel {
        return raspberryPiStats.getRaspberryPiStats()
    }

    @Tag(name="Weerlive")
    @GetMapping("/weerlive/current")
    fun weerlive(): WeerLiveModel? {
        return weerLive.getWeerLiveData()
    }

    @Tag(name="Eneco")
    @PostMapping("/eneco/update")
    fun enecoDataPost(@RequestBody source: String): List<EnecoConsumption> {
        return enecoUpdateService.updateEnecoStatistics(source)
    }

    @Tag(name="Log")
    @GetMapping("/logs")
    fun getLogs(): List<LogLine> {
        return logService.getLogs()
    }

}