package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.electricity.restclient.HomeWizardElectricityClient
import ms.homemonitor.electricity.restclient.model.HomeWizardElectricityData
import ms.homemonitor.heath.repository.model.ManualMeasuredHeathModel
import ms.homemonitor.heath.restclient.EnecoRestClient
import ms.homemonitor.heath.restclient.model.EnecoConsumption
import ms.homemonitor.heath.service.HeathService
import ms.homemonitor.system.cliclient.DropboxClient
import ms.homemonitor.system.cliclient.SystemTemperatureClient
import ms.homemonitor.system.cliclient.model.BackupDataModel
import ms.homemonitor.system.cliclient.model.SystemTemperatureModel
import ms.homemonitor.system.service.SystemService
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.tado.restclient.model.TadoDayReport
import ms.homemonitor.tado.restclient.model.TadoResponseModel
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate


@RestController
class ControllerVerify(
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val tadoRestClient: TadoClient,
    private val enecoRestClient: EnecoRestClient,
    private val systemTemperatureClient: SystemTemperatureClient,
    private val dropboxClient: DropboxClient,
    private val systemService: SystemService,
    private val heathService: HeathService
) {

    @Tag(name="1. Homewizard")
    @GetMapping("/verify/homewizard/electricity/current")
    fun homeWizardElectricity(): HomeWizardElectricityData {
        return homeWizardElectricityClient.getHomeWizardElectricityData()
    }

    @Tag(name="1. Homewizard")
    @GetMapping("/verify/homewizard/water/current")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardWaterClient.getHomeWizardWaterData()
    }

    @Tag(name="2. Eneco")
    @GetMapping("/verify/eneco/current")
    @Operation(summary = "Be careful using this one." +
            "It uses Selenium to log in to 'mijneneco'. " +
            "Using it many times after each other might lead to 'my account'  to be blocked")
    fun getEnecoData(): List<EnecoConsumption> {
        return enecoRestClient.getNewDataFromEneco(LocalDate.now())
    }

    @Tag(name="2. Eneco")
    @PostMapping("/verify/eneco/manual")
    @Operation(summary = "Set a manual measurement")
    fun setManualEnecoMeasurement(@RequestBody manualStanding: ManualMeasuredHeathModel) {
        val resultOk = heathService.setManualMeasurement(manualStanding)
        if (!resultOk) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }


    @Tag(name="3. Tado")
    @GetMapping("/verify/tado/current")
    fun tado(): TadoResponseModel {
        return tadoRestClient.getTadoResponse()
    }

    @Tag(name="3. Tado")
    @GetMapping("/verify/tado/dayreport")
    fun tadoHistorical(@RequestParam(name="day", required = false) inputDay: String = LocalDate.now().toString()): TadoDayReport {
        try {
            val dayTime = LocalDate.parse(inputDay)
            return tadoRestClient.getTadoHistoricalInfo(dayTime)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        }
    }

    @Tag(name="4. System")
    @GetMapping("/verify/system/temperature/current")
    fun raspberrypi(): SystemTemperatureModel {
        return systemTemperatureClient.getSystemTemperature()
    }

    @Tag(name="4. System")
    @GetMapping("/verify/backupprocess/current")
    fun getBackupStats(): List<BackupDataModel> {
        return dropboxClient.getBackupStats()
    }

    @Tag(name="4. System")
    @GetMapping("/verify/backupprocess/space")
    fun getFreeBackupSpace(): Long {
        return dropboxClient.getFreeBackupSpace()
    }

    @Tag(name="4. System")
    @PostMapping("/verify/backup")
    fun executeBackup() {
        systemService.executeBackup()
    }

    @Tag(name="4. System")
    @DeleteMapping("/verify/backup/cleanup")
    fun cleanupBackup(@RequestParam keep: Int) {
        systemService.cleanUp(keep)
    }
}