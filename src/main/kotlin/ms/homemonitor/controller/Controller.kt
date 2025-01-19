package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import ms.homemonitor.dbstats.cliclient.model.BackupStats
import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.eneco.domain.service.EnecoService
import ms.homemonitor.power.domain.service.HomeWizardPowerService
import ms.homemonitor.power.restclient.model.HomeWizardEnergyData
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import ms.homemonitor.power.restclient.HomeWizardEnergyClient
import ms.homemonitor.log.domain.service.LogService
import ms.homemonitor.log.domain.model.LogLine
import ms.homemonitor.raspberrypi.domain.model.RaspberryPiStatsModel
import ms.homemonitor.raspberrypi.cliclient.RaspberryPiStats
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.tado.restclient.model.TadoResponseModel
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.water.domain.service.HomeWizardWaterService
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller(
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

    private val log = LoggerFactory.getLogger(Controller::class.java)

    @Tag(name="Admin")
    @GetMapping("/ping")
    fun ping() {
        return
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/energy/current")
    fun homeWizardEnergy(): HomeWizardEnergyData {
        return homeWizardEnergyClient.getHomeWizardEnergyData()
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/water/current")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardWaterClient.getHomeWizardWaterData()
    }

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

    @Tag(name="Raspberry Pi")
    @GetMapping("/backup/stats")
    fun getBackupStats(): List<BackupStats> {
        return dbStats.getBackupStats()
    }

    @Tag(name="Eneco")
    @GetMapping("/updateEnecoStatistics")
    @Transactional
    fun updateEnecoStatistics() {
        enecoService.updateEnecoStatistics()
    }

    @Tag(name="Eneco")
    @GetMapping("/eneco/summary")
    fun getHeathSummary(): YearSummary {
        return enecoService.getYearSummary()
    }

    @Tag(name="Log")
    @GetMapping("/logs")
    fun getLogs(): List<LogLine> {
        return logService.getLogs()
    }


    @Tag(name="Test")
    @GetMapping("/test")
    fun someTest(): Any {
        return "acn be used for test purposes"
    }

}