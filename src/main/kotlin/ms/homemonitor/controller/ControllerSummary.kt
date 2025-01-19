package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.eneco.domain.service.EnecoService
import ms.homemonitor.power.domain.service.HomeWizardPowerService
import ms.homemonitor.power.restclient.HomeWizardEnergyClient
import ms.homemonitor.log.domain.service.LogService
import ms.homemonitor.raspberrypi.cliclient.RaspberryPiStats
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.water.domain.service.HomeWizardWaterService
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerSummary(
    private val homeWizardEnergyClient: HomeWizardEnergyClient,
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val homeWizardPowerService: HomeWizardPowerService,
    private val homeWizardWaterService: HomeWizardWaterService,
    private val tadoDataProvider: TadoClient,
    private val raspberryPiStats: RaspberryPiStats,
    private val enecoService: EnecoService,
    private val logService: LogService,
    private val dbStats: DbStats,
) {

    private val log = LoggerFactory.getLogger(ControllerSummary::class.java)

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/energy/summary")
    fun getPowerSummary(): YearSummary {
        return homeWizardPowerService.getPowerYearSummary()
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/water/summary")
    fun getWaterSummary(): YearSummary {
        return homeWizardWaterService.getWaterYearSummary()
    }
    @Tag(name="Eneco")
    @GetMapping("/eneco/summary")
    fun getHeathSummary(): YearSummary {
        return enecoService.getYearSummary()
    }

}