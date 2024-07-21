package ms.powermonitoring.service

import ms.powermonitoring.homewizard.rest.HomeWizard
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class PowerMonitoringService(
    private val homeWizard: HomeWizard,
    environment: Environment) {

    private val enabled = environment.getProperty("low-level.csv.enabled")?.toBoolean() ?: false

    fun storeLatestPowerMeasurement() {

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)

        val response = homeWizard.getHomeWizardData()

        if (enabled) {
            print("$current;")
            print("${response.totalPowerImportKwh};")
            print("${response.totalPowerImportT1Kwh};")
            print("${response.totalPowerImportT2Kwh};")
            print("${response.activePowerWatt};")
            print("${response.activePowerL1Watt};")
            print("${response.activePowerL2Watt};")
            print("${response.activePowerL3Watt};")
        }

        println()
    }
}