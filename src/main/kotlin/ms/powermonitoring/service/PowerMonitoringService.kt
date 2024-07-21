package ms.powermonitoring.service

import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.rest.HomeWizard
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class PowerMonitoringService(
    private val homeWizard: HomeWizard,
    environment: Environment
) {

    private val fileNameMostDetailed = environment.getProperty("most-detailed.output-file-name") ?: "defaultMostDetailed.csv"
    private val fileNameHour = environment.getProperty("hour.output-file-name") ?: "defaultHour.csv"
    private val fileNameDay = environment.getProperty("day.output-file-name") ?: "defaultDay.csv"
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun storeMostDetailedPowerMeasurement() {
        File(fileNameMostDetailed)
            .appendText(homeWizard.getHomeWizardData().toCSV(includingActivePower = true))
    }

    fun storeHourPowerMeasurement() {
        File(fileNameHour)
            .appendText(homeWizard.getHomeWizardData().toCSV(includingActivePower = false))
    }
    fun storeDayPowerMeasurement() {
        File(fileNameDay)
            .appendText(homeWizard.getHomeWizardData().toCSV(includingActivePower = false))
    }


    private fun HomeWizardMeasurementData.toCSV(includingActivePower: Boolean): String {
        val response = this
        var result =  LocalDateTime.now().format(formatter) +
                "${response.totalPowerImportKwh};" +
                "${response.totalPowerImportT1Kwh};" +
                "${response.totalPowerImportT2Kwh};"
                if (includingActivePower) {
                    result += "${response.activePowerWatt};" +
                            "${response.activePowerL1Watt};" +
                            "${response.activePowerL2Watt};" +
                            "${response.activePowerL3Watt};"
                }
                result += "\n"
        return result
    }
}