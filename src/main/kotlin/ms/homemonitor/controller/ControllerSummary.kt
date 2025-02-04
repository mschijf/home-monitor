package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.electricity.service.HomeWizardElectricityService
import ms.homemonitor.heath.service.HeathService
import ms.homemonitor.shared.summary.service.model.YearSummary
import ms.homemonitor.water.service.HomeWizardWaterService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerSummary(
    private val homeWizardElectricityService: HomeWizardElectricityService,
    private val homeWizardWaterService: HomeWizardWaterService,
    private val heathService: HeathService,
) {

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
        return heathService.getYearSummary()
    }

}