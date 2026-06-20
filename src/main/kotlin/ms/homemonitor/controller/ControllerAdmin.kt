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
import ms.homemonitor.water.service.HomeWizardWaterService
import ms.homemonitor.water.service.model.ShowerSession
import ms.homemonitor.weather.restclient.WeatherApiClient
import ms.homemonitor.weather.restclient.model.WeatherApiCurrentData
import org.springframework.http.HttpStatus
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RestController
class ControllerAdmin(
    private val homeWizardElectricityClient: HomeWizardElectricityClient,
    private val homeWizardWaterClient: HomeWizardWaterClient,
    private val homeWizardWaterService: HomeWizardWaterService,
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
    private val weatherApiClient: WeatherApiClient,
) {

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private fun parseDate(dayParam: String?): LocalDate =
        try {
            if (dayParam != null) LocalDate.parse(dayParam, formatter) else LocalDate.now()
        } catch (_: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'day' must be in format dd-MM-yyyy")
        }


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

    @Tag(name="1. Homewizard")
    @GetMapping("/admin/water/showers")
    @Operation(
        summary = "Detect shower sessions for a given day",
        description = "Returns water usage sessions for the given day that are likely showers " +
                "(>= 20 liter, flow >= 1 L/min, warmth > 0). Gaps of up to 3 minutes within a session are allowed."
    )
    fun getShowers(
        @RequestParam(name = "day", required = false) dayParam: String?,
    ): List<ShowerSession> {
        return homeWizardWaterService.getShowers(parseDate(dayParam))
    }

    @Tag(name="1. Homewizard")
    @PostMapping("/admin/water/initshowers")
    @Operation(
        summary = "Init shower sessions from a given day",
        description = "Returns water usage sessions for the given day that are likely showers " +
                "(>= 20 liter, flow >= 1 L/min, warmth > 0). Gaps of up to 3 minutes within a session are allowed."
    )
    fun initShowersTable(
        @RequestParam(name = "fromDay", required = false) fromDayParam: String?,
    ) {
        return homeWizardWaterService.initShowers(parseDate(fromDayParam))
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

    @Tag(name="3a. Tado - authentication")
    @PostMapping("/admin/tado/getAccessDeviceUrl")
    @Operation(
        summary = "Step 1 in re-assigning a device to tado. Use if tado does not repsond anymore",
        description = "" +
                " See: https://support.tado.com/en/articles/8565472-how-do-i-authenticate-to-access-the-rest-api\n" +
                "\n" +
                " 1. execute via swagger: /admin/tado/getAccessDeviceUrl\n" +
                "   you get a response like\n" +
                "```\n" +
                "          {\n" +
                "            \"device_code\": \" ..... \",\n" +
                "            \"expires_in\": \"300\",\n" +
                "            \"interval\": 5,\n" +
                "            \"user_code\": \"ABCDEFG\",\n" +
                "            \"verification_uri\": \"https://login.tado.com/oauth2/device\",\n" +
                "            \"verification_uri_complete\": \"https://login.tado.com/oauth2/device?user_code=ABCDEFG\"\n" +
                "          }\n" +
                "```\n" +
                " 2. goto the url presented at 'verification_uri_complete' (so, in this example: https://login.tado.com/oauth2/device?user_code=ABCDEFG)\n" +
                " 3. submit the user_code, and login with tado credentials (in 1password)\n" +
                "\n" +
                " 4. final step: execute via swagger: /admin/tado/confirmDevice\n" +
                "/\n")
    fun tadoDeviceAuthorization(): Any? {
        return tadoAccessToken.newTadoAccessDeviceAuthorization()
    }

    @Tag(name="3a. Tado - authentication")
    @PostMapping("/admin/tado/confirmDevice")
    @Operation(
        summary = "Step 2 in re-assigning a device to tado. Use if tado does not repsond anymore",
        description = "Just press execute, *after* you have done the steps described at Step 1")
    fun tadoConfirmDeviceAuthorization(): Any? {
        return tadoAccessToken.confirmNewTadoAccessDeviceAuthorization()
    }

    @Tag(name="3b. Tado")
    @GetMapping("/admin/tado/current")
    fun tado(): TadoResponseModel {
        return tadoRestClient.getTadoResponse()
    }

    @Tag(name="3b. Tado")
    @GetMapping("/admin/tado/device")
    fun tadoDevice(): TadoDevice {
        return tadoRestClient.getTadoDeviceInfo()
    }

    @Tag(name="3b. Tado")
    @PostMapping("/admin/tado/device")
    fun tadoStoreDeviceInfo(): TadoDevice {
        tadoService.processDeviceInfo()
        return tadoRestClient.getTadoDeviceInfo()
    }

    @Tag(name="3b. Tado")
    @GetMapping("/admin/tado/dayreport")
    fun tadoHistorical(@RequestParam(name="day", required = false) inputDay: String = LocalDate.now().toString()): TadoDayReport {
        return tadoRestClient.getTadoHistoricalInfo(parseDate(inputDay))
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

        val date = parseDate(dateString)
        return tuyaClient.getTuyaData(deviceId, date.atStartOfDay(), date.atStartOfDay().plusHours(24))
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

    @Tag(name="7. Weatherapi")
    @GetMapping("/admin/weather/current")
    fun getCurrent(): WeatherApiCurrentData {
        return weatherApiClient.getCurrentWeather()
    }

}