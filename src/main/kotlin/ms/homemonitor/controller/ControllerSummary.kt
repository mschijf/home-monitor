package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.eneco.domain.service.EnecoService
import ms.homemonitor.electricity.domain.service.HomeWizardElectricityService
import ms.homemonitor.electricity.restclient.HomeWizardElectricityClient
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
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val homeWizardElectricityService: HomeWizardElectricityService,
    private val homeWizardWaterService: HomeWizardWaterService,
    private val tadoDataProvider: TadoClient,
    private val raspberryPiStats: RaspberryPiStats,
    private val enecoService: EnecoService,
    private val logService: LogService,
    private val dbStats: DbStats,
) {

    private val log = LoggerFactory.getLogger(ControllerSummary::class.java)

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/electricity/summary")
    fun getElectricitySummary(): YearSummary {
        return homeWizardElectricityService.getElectricityYearSummary()
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