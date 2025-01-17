package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.dbstats.cliclient.model.BackupStats
import ms.homemonitor.eneco.restclient.EnecoRestClient
import ms.homemonitor.eneco.restclient.model.EnecoConsumption
import ms.homemonitor.electricity.restclient.HomeWizardElectricityClient
import ms.homemonitor.electricity.restclient.model.HomeWizardElectricityData
import ms.homemonitor.raspberrypi.cliclient.RaspberryPiStats
import ms.homemonitor.raspberrypi.domain.model.RaspberryPiStatsModel
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.tado.restclient.model.TadoResponseModel
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate


@RestController
class ControllerTest(
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val tadoRestClient: TadoClient,
    private val enecoRestClient: EnecoRestClient,
    private val raspberryPiStats: RaspberryPiStats,
    private val dbStats: DbStats,
) {

    private val log = LoggerFactory.getLogger(ControllerTest::class.java)

    @Tag(name="Homewizard")
    @GetMapping("/test/homewizard/electricity/current")
    fun homeWizardElectricity(): HomeWizardElectricityData {
        return homeWizardElectricityClient.getHomeWizardElectricityData()
    }

    @Tag(name="Homewizard")
    @GetMapping("/test/homewizard/water/current")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardWaterClient.getHomeWizardWaterData()
    }

    @Tag(name="Eneco")
    @GetMapping("/test/eneco/current")
    @Operation(summary = "Be careful using this one." +
            "It uses Selenium to log in to 'mijneneco'. " +
            "Using it many times after each other can causes my account  to be blocked")
    fun getEnecoData(): List<EnecoConsumption> {
        return enecoRestClient.getNewDataFromEneco(LocalDate.now())
    }

    @Tag(name="Tado")
    @GetMapping("/test/tado/current")
    fun tado(): TadoResponseModel {
        return tadoRestClient.getTadoResponse()
    }

    @Tag(name="Raspberry Pi")
    @GetMapping("/test/raspberrypi/current")
    fun raspberrypi(): RaspberryPiStatsModel {
        return raspberryPiStats.getRaspberryPiStats()
    }

    @Tag(name="Raspberry Pi")
    @GetMapping("/test/backupprocess/current")
    fun getBackupStats(): List<BackupStats> {
        return dbStats.getBackupStats()
    }

    @Tag(name="Test")
    @GetMapping("/test/play_new_stuff")
    fun someTest(): Any {
        return "Can be used for test purposes. Currently doing nothing"
    }

}