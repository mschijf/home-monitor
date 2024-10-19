package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.domain.eneco.model.EnecoConsumption
import ms.homemonitor.domain.eneco.EnecoService
import ms.homemonitor.domain.eneco.EnecoUpdateService
import ms.homemonitor.domain.homewizard.model.HomeWizardEnergyData
import ms.homemonitor.domain.homewizard.model.HomeWizardWaterData
import ms.homemonitor.domain.homewizard.rest.HomeWizard
import ms.homemonitor.domain.raspberrypi.rest.RaspberryPiStats
import ms.homemonitor.domain.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.domain.tado.model.TadoResponseModel
import ms.homemonitor.domain.tado.rest.Tado
import ms.homemonitor.domain.weerlive.model.WeerLiveModel
import ms.homemonitor.domain.weerlive.rest.WeerLive
import ms.homemonitor.domain.log.LogService
import ms.homemonitor.domain.log.model.LogLine
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

    @Tag(name="Eneco")
    @GetMapping("/eneco/consumption/hour")
    fun enecoDataJSONHourConsumption(
        @RequestParam from: String? = null,
        @RequestParam to: String? = null): List<EnecoConsumption> {

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
        @RequestParam to: String? = null): List<EnecoConsumption> {

        val fromDateTime = stringToLocalDateTime(from, LocalDateTime.MIN, "Problem with from request parameter: $from")
        val toDateTime = stringToLocalDateTime(to, LocalDateTime.MAX, "Problem with to request parameter: $to")

        return enecoService.getEnecoDayConsumption(fromDateTime, toDateTime)
    }

    @Tag(name="Eneco")
    @GetMapping("/eneco/consumption/cumulative/day")
    fun enecoDataJSONDayCumulativeConsumption(
        @RequestParam from: String? = null,
        @RequestParam to: String? = null): List<EnecoConsumption> {

        val fromDateTime = stringToLocalDateTime(from, LocalDateTime.MIN, "Problem with from request parameter: $from")
        val toDateTime = stringToLocalDateTime(to, LocalDateTime.MAX, "Problem with to request parameter: $to")
        return enecoService.getEnecoCumulativeDayConsumption(fromDateTime, toDateTime)
    }

    @Tag(name="Log")
    @GetMapping("/logs")
    fun getLogs(): List<LogLine> {
        return logService.getLogs()
    }

}