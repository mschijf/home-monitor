package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.electricity.restclient.HomeWizardElectricityClient
import ms.homemonitor.electricity.restclient.model.HomeWizardElectricityData
import ms.homemonitor.heath.repository.model.ManualHeathCorrectionModel
import ms.homemonitor.heath.restclient.EnecoRestClient
import ms.homemonitor.heath.restclient.model.EnecoConsumption
import ms.homemonitor.heath.service.HeathService
import ms.homemonitor.shelly.restclient.ShellyClient
import ms.homemonitor.shelly.restclient.model.ShellyThermometerData
import ms.homemonitor.smartplug.restclient.TuyaClient
import ms.homemonitor.smartplug.restclient.model.TuyaDataDetail
import ms.homemonitor.smartplug.restclient.model.TuyaDeviceMasterData
import ms.homemonitor.system.cliclient.DropboxClient
import ms.homemonitor.system.cliclient.SystemTemperatureClient
import ms.homemonitor.system.cliclient.model.BackupDataModel
import ms.homemonitor.system.cliclient.model.SystemTemperatureModel
import ms.homemonitor.system.service.SystemService
import ms.homemonitor.tado.restclient.TadoAccessToken
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.tado.restclient.model.TadoDayReport
import ms.homemonitor.tado.restclient.model.TadoDevice
import ms.homemonitor.tado.restclient.model.TadoResponseModel
import ms.homemonitor.tado.service.TadoService
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import org.springframework.http.HttpStatus
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@RestController
class ControllerAdmin(
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val tadoAccessToken: TadoAccessToken,
    private val tadoRestClient: TadoClient,
    private val shellyRestClient: ShellyClient,
    private val enecoRestClient: EnecoRestClient,
    private val systemTemperatureClient: SystemTemperatureClient,
    private val dropboxClient: DropboxClient,
    private val systemService: SystemService,
    private val heathService: HeathService,
    private val tadoService: TadoService,
    private val tuyaClient: TuyaClient,
) {

    @GetMapping("/")
    fun home(model: ModelMap): ModelAndView {
        model.addAttribute("attribute", "redirectWithRedirectPrefix")
        return ModelAndView("redirect:/swagger-ui/index.html", model)
    }

    @Tag(name="1. Homewizard")
    @GetMapping("/admin/homewizard/electricity/current")
    fun homeWizardElectricity(): HomeWizardElectricityData {
        return homeWizardElectricityClient.getHomeWizardElectricityData()
    }

    @Tag(name="1. Homewizard")
    @GetMapping("/admin/homewizard/water/current")
    fun homeWizardWater(): HomeWizardWaterData {
        return homeWizardWaterClient.getHomeWizardWaterData()
    }

    @Tag(name="2. Eneco")
    @GetMapping("/admin/eneco/current")
    @Operation(summary = "Be careful using this one." +
            "It uses Selenium to log in to 'mijneneco'. " +
            "Using it many times after each other might lead to 'my account'  to be blocked")
    fun getEnecoData(): List<EnecoConsumption> {
        return enecoRestClient.getNewDataFromEneco(LocalDate.now())
    }

    @Tag(name="2. Eneco")
    @PostMapping("/admin/eneco/correction")
    @Operation(summary = "Set a manual measurement/correction")
    fun setManualEnecoMeasurement(@RequestBody manualStanding: ManualHeathCorrectionModel) {
        val resultOk = heathService.setManualCorrection(manualStanding)
        if (!resultOk) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }
    }

    @Tag(name="3. Tado")
    @PostMapping("/admin/tado/getAccessDeviceUrl")
    fun tadoDeviceAuthorization(): Any? {
        return tadoAccessToken.newTadoAccessDeviceAuthorization()
    }

    @Tag(name="3. Tado")
    @PostMapping("/admin/tado/confirmDevice")
    fun tadoConfirmDeviceAuthorization(): Any? {
        return tadoAccessToken.confirmNewTadoAccessDiviceAuthorization()
    }

    @Tag(name="3. Tado")
    @GetMapping("/admin/tado/current")
    fun tado(): TadoResponseModel {
        return tadoRestClient.getTadoResponse()
    }

    @Tag(name="3. Tado")
    @GetMapping("/admin/tado/device")
    fun tadoDevice(): TadoDevice {
        return tadoRestClient.getTadoDeviceInfo()
    }

    @Tag(name="3. Tado")
    @PostMapping("/admin/tado/device")
    fun tadoStoreDeviceInfo(): TadoDevice {
        tadoService.processDeviceInfo()
        return tadoRestClient.getTadoDeviceInfo()
    }

    @Tag(name="3. Tado")
    @GetMapping("/admin/tado/dayreport")
    fun tadoHistorical(@RequestParam(name="day", required = false) inputDay: String = LocalDate.now().toString()): TadoDayReport {
        try {
            val dayTime = LocalDate.parse(inputDay)
            return tadoRestClient.getTadoHistoricalInfo(dayTime)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message, e)
        }
    }

    @Tag(name="4. Shelly")
    @GetMapping("/admin/shelly/status")
    fun shellyStatus(): ShellyThermometerData {
        return shellyRestClient.getShellyThermometerData()
    }

    @Tag(name="5. Tuya")
    @GetMapping("/admin/tuya/devices/masterdata")
    fun getTuyaDeviceMasterData(): List<TuyaDeviceMasterData> {
        return tuyaClient.getTuyaDeviceMasterData()
    }

    @Tag(name="5. Tuya")
    @GetMapping("/admin/tuya/devices/{deviceId}")
    fun getTuyaData(@PathVariable deviceId: String,
                    @Parameter(description = "date in format dd-mm-yyyy") @RequestParam dateString: String): List<TuyaDataDetail> {

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        try {
            val date = LocalDate.parse(dateString, formatter)
            return tuyaClient.getTuyaData(deviceId, date.atStartOfDay(), date.atStartOfDay().plusHours(24))
        } catch (_: DateTimeParseException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "'dateString' parameter cannot be paresed to a date")
        }
    }

    @Tag(name="6. System")
    @GetMapping("/admin/system/temperature/current")
    fun raspberrypi(): SystemTemperatureModel {
        return systemTemperatureClient.getSystemTemperature()
    }

    @Tag(name="6. System")
    @GetMapping("/admin/backupprocess/current")
    fun getBackupStats(): List<BackupDataModel> {
        return dropboxClient.getBackupStats()
    }

    @Tag(name="6. System")
    @GetMapping("/admin/backupprocess/space")
    fun getFreeBackupSpace(): Long {
        return dropboxClient.getFreeBackupSpace()
    }

    @Tag(name="6. System")
    @PostMapping("/admin/backup")
    fun executeBackup() {
        systemService.executeBackup()
    }

    @Tag(name="6. System")
    @DeleteMapping("/admin/backup/cleanup")
    fun cleanupBackup(@RequestParam keep: Int) {
        systemService.cleanUp(keep)
    }
}