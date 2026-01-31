package ms.homemonitor.shared.scheduler

import ms.homemonitor.electricity.service.HomeWizardElectricityService
import ms.homemonitor.heath.service.HeathService
import ms.homemonitor.shelly.service.ShellyService
import ms.homemonitor.smartplug.service.SmartPlugService
import ms.homemonitor.system.service.SystemService
import ms.homemonitor.tado.service.TadoDayReportService
import ms.homemonitor.tado.service.TadoService
import ms.homemonitor.water.service.HomeWizardWaterService
import ms.homemonitor.weather.service.WeatherService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class Scheduler(
    private val homeWizardWaterService: HomeWizardWaterService,
    private val weatherService: WeatherService,
    private val tadoDayReportService: TadoDayReportService,
    private val tadoService: TadoService,
    private val systemService: SystemService,
    private val smartPlugService: SmartPlugService,
    private val shellyService: ShellyService,
    private val heathService: HeathService,
    private val homeWizardElectricityService: HomeWizardElectricityService
): AbstractBaseScheduler() {


    //-----------------------------------------------------------------------------------------------------------------
    // Electricity
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.electricity.detailed}")
    fun detailedElectricityMeasurement()  = runSafely { homeWizardElectricityService.processMeasurement(persistentStore = false) }

    @Scheduled(cron = "\${home-monitor.scheduler.electricity.regular}")
    fun minuteMeasurement()  = runSafely { homeWizardElectricityService.processMeasurement(persistentStore = true) }

    @Scheduled(cron = "\${home-monitor.scheduler.electricity.cleanup}")
    fun electricityCleanup()  = runSafely { homeWizardElectricityService.cleanupOldData(keepDays = 90L) }

    //-----------------------------------------------------------------------------------------------------------------
    // Heath
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.heath.updateEnecoStats}")
    fun updateEnecoStatistics()  = runSafely { heathService.processMeaurement() }

    //-----------------------------------------------------------------------------------------------------------------
    // Shelly
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.shelly.regular}")
    fun shellyMeasurement()  = runSafely { shellyService.processMeasurement() }

    //-----------------------------------------------------------------------------------------------------------------
    // Smartplug
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.smartPlug.regular}")
    fun retrieveSmartPlugData()  = runSafely { smartPlugService.processMeasurement() }

    //-----------------------------------------------------------------------------------------------------------------
    // System
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.system.dbStats}")
    fun dbStats()  = runSafely { systemService.processDbStats() }

    @Scheduled(cron = "\${home-monitor.scheduler.system.backup}")
    fun doBackup() = runSafely {
        systemService.executeBackup()
        systemService.cleanUp()
    }

    @Scheduled(cron = "\${home-monitor.scheduler.system.dropboxFreeSpace}")
    fun dropboxFreeSpace() = runSafely { systemService.processBackupStats() }

    @Scheduled(cron = "\${home-monitor.scheduler.system.temperature}")
    fun systemTemperatureMeasurement()  = runSafely { systemService.processMeasurement() }

    //-----------------------------------------------------------------------------------------------------------------
    // Tado
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.tado.regular}")
    fun tadoMeasurement() = runSafely { tadoService.processMeasurement() }

    @Scheduled(cron = "\${home-monitor.scheduler.tado.hourSummary}")
    fun tadoMeasurementHour() = runSafely { tadoDayReportService.processHourAggregateMeasurement() }

    @Scheduled(cron = "\${home-monitor.scheduler.tado.deviceState}")
    fun tadoBattery() = runSafely { tadoService.processDeviceInfo() }

    @Scheduled(cron = "\${home-monitor.scheduler.tado.cleanup}")
    fun tadoCleanup() = runSafely { tadoService.cleanupOldData(keepDays = 90L) }

    //-----------------------------------------------------------------------------------------------------------------
    // Water
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.water.detailed}")
    fun detailedWaterMeasurement() = runSafely { homeWizardWaterService.processMeasurement(false) }

    @Scheduled(cron = "\${home-monitor.scheduler.water.regular}")
    fun minuteWaterMeasurement() = runSafely { homeWizardWaterService.processMeasurement(true) }

    @Scheduled(cron = "\${home-monitor.scheduler.water.cleanup}")
    fun waterCleanup() = runSafely { homeWizardWaterService.cleanupOldData(90) }

    //-----------------------------------------------------------------------------------------------------------------
    // Weather
    //-----------------------------------------------------------------------------------------------------------------
    @Scheduled(cron = "\${home-monitor.scheduler.weather.regular}")
    fun weatherMeasurement()  = runSafely { weatherService.processMeasurement() }
}