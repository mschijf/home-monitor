package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.infra.eneco.model.EnecoDayConsumption
import ms.homemonitor.infra.homewizard.model.HomeWizardEnergyData
import ms.homemonitor.infra.homewizard.model.HomeWizardWaterData
import ms.homemonitor.infra.homewizard.rest.HomeWizard
import ms.homemonitor.infra.raspberrypi.RaspberryPiStats
import ms.homemonitor.infra.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.infra.tado.model.TadoResponseModel
import ms.homemonitor.infra.tado.rest.Tado
import ms.homemonitor.infra.weerlive.model.WeerLiveModel
import ms.homemonitor.infra.weerlive.rest.WeerLive
import ms.homemonitor.service.EnecoService
import ms.homemonitor.service.EnecoUpdateService
import ms.homemonitor.service.LogLine
import ms.homemonitor.service.LogService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RestController
class Controller(
    private val homeWizardDataProvider: HomeWizard,
    private val tadoDataProvider: Tado,
    private val raspberryPiStats: RaspberryPiStats,
    private val weerLive: WeerLive,
    private val enecoUpdateService: EnecoUpdateService,
    private val enecoService: EnecoService,
    private val logService: LogService
) {

    private val log = LoggerFactory.getLogger(Controller::class.java)

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
    fun enecoDataPost(@RequestBody source: String): List<EnecoDayConsumption> {
        return enecoUpdateService.updateEnecoStatistics(source)
    }

    @Tag(name="Eneco")
    @GetMapping("/eneco/consumption/hour")
    fun enecoDataJSONHourConsumption(
        @RequestParam from: String? = null,
        @RequestParam to: String? = null): List<EnecoDayConsumption> {

        val fromDateTime = stringToLocalDateTime(from, LocalDateTime.MIN, "Problem with from request parameter: $from")
        val toDateTime = stringToLocalDateTime(to, LocalDateTime.MAX, "Problem with to request parameter: $to")

        return enecoService.getEnecoHourConsumption(fromDateTime, toDateTime)
    }

    private fun stringToLocalDateTime(stringDate: String?, defaultValue: LocalDateTime, errorMessage: String): LocalDateTime {
        return try {
            LocalDateTime.parse(stringDate!!, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            if (stringDate != null) {
                log.warn(errorMessage)
            }
            defaultValue
        }
    }

    @Tag(name="Eneco")
    @GetMapping("/eneco/consumption/day")
    fun enecoDataJSONDayConsumption(
        @RequestParam from: String? = null,
        @RequestParam to: String? = null): List<EnecoDayConsumption> {

        val fromDateTime = stringToLocalDateTime(from, LocalDateTime.MIN, "Problem with from request parameter: $from")
        val toDateTime = stringToLocalDateTime(to, LocalDateTime.MAX, "Problem with to request parameter: $to")

        return enecoService.getEnecoDayConsumption(fromDateTime, toDateTime)
    }

    @Tag(name="Eneco")
    @GetMapping("/eneco/consumption/cumulative/day")
    fun enecoDataJSONDayCumulativeConsumption(): List<EnecoDayConsumption> {
        return enecoService.getEnecoCumulativeDayConsumption()
    }

    @Tag(name="Log")
    @GetMapping("/logs")
    fun getLogs(): List<LogLine> {
        return logService.getLogs()
    }

}