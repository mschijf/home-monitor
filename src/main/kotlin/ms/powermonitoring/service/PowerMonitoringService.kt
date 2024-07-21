package ms.powermonitoring.service

import ms.powermonitoring.config.ApplicationOutputProperties
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.rest.HomeWizard
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class PowerMonitoringService(
    private val homeWizard: HomeWizard,
    private val applicationOutputProperties: ApplicationOutputProperties
) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun variableTimedPowerMeasurement() {
        File(applicationOutputProperties.variableTimeFileName)
            .appendText(homeWizard.getHomeWizardData().toCSV(includingActivePower = true))
    }

    fun storeHourPowerMeasurement() {
        File(applicationOutputProperties.hourFileName)
            .appendText(homeWizard.getHomeWizardData().toCSV(includingActivePower = false))
    }
    fun storeDayPowerMeasurement() {
        File(applicationOutputProperties.dayFileName)
            .appendText(homeWizard.getHomeWizardData().toCSV(includingActivePower = false))
    }


    private fun HomeWizardMeasurementData.toCSV(includingActivePower: Boolean): String {
        val response = this
        var result =  "${LocalDateTime.now().format(formatter)};" +
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