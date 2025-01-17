package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import ms.homemonitor.domain.eneco.domain.service.EnecoService
import ms.homemonitor.domain.homewizard.model.HomeWizardEnergyData
import ms.homemonitor.domain.homewizard.model.HomeWizardWaterData
import ms.homemonitor.domain.homewizard.rest.HomeWizard
import ms.homemonitor.domain.log.LogService
import ms.homemonitor.domain.log.model.LogLine
import ms.homemonitor.domain.dbstats.model.BackupStats
import ms.homemonitor.domain.dbstats.rest.DbStats
import ms.homemonitor.domain.homewizard.HomeWizardService
import ms.homemonitor.domain.raspberrypi.model.RaspberryPiStatsModel
import ms.homemonitor.domain.raspberrypi.rest.RaspberryPiStats
import ms.homemonitor.domain.summary.model.YearSummary
import ms.homemonitor.domain.tado.model.TadoResponseModel
import ms.homemonitor.domain.tado.rest.Tado
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller(
    private val homeWizardDataProvider: HomeWizard,
    private val homeWizardService: HomeWizardService,
    private val tadoDataProvider: Tado,
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
        return homeWizardDataProvider.getHomeWizardEnergyData()
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/water/current")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardDataProvider.getHomeWizardWaterData()
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/energy/summary")
    fun getPowerSummary(): YearSummary {
        return homeWizardService.getPowerYearSummary()
    }

    @Tag(name="Homewizard")
    @GetMapping("/homewizard/water/summary")
    fun getWaterSummary(): YearSummary {
        return homeWizardService.getWaterYearSummary()
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
        return tadoDataProvider.getTadoTest()
    }

}