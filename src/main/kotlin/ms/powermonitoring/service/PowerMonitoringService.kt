package ms.powermonitoring.service

import ms.powermonitoring.config.ApplicationOutputProperties
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.rest.HomeWizard
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.DecimalFormat



@Service
class PowerMonitoringService(
    private val homeWizard: HomeWizard,
    private val applicationOutputProperties: ApplicationOutputProperties
) {

    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#.000")

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
        var result =  "${LocalDateTime.now().format(timeFormat)};" +
                "${decimalFormat.format(response.totalPowerImportKwh)};" +
                "${decimalFormat.format(response.totalPowerImportT1Kwh)};" +
                "${decimalFormat.format(response.totalPowerImportT2Kwh)};"
                if (includingActivePower) {
                    result += "${decimalFormat.format(response.activePowerWatt)};" +
                            "${decimalFormat.format(response.activePowerL1Watt)};" +
                            "${decimalFormat.format(response.activePowerL2Watt)};" +
                            "${decimalFormat.format(response.activePowerL3Watt)};"
                }
                result += "\n"
        return result
    }
}