package ms.powermonitoring.service

import ms.powermonitoring.config.ApplicationOutputProperties
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.rest.HomeWizard
import org.slf4j.LoggerFactory
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
    private val log = LoggerFactory.getLogger(javaClass)
    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

    fun variableTimedPowerMeasurement() {
        appendToFile(applicationOutputProperties.variableTimeFileName, includingActivePower = true)
    }

    fun storeHourPowerMeasurement() {
        appendToFile(applicationOutputProperties.hourFileName)
    }

    fun storeDayPowerMeasurement() {
        appendToFile(applicationOutputProperties.dayFileName)
    }

    fun appendToFile(fileName: String, includingActivePower: Boolean=false) {
        val resultMkdirs: Boolean = File(applicationOutputProperties.path).mkdirs()
        if (resultMkdirs) {
            log.info("created the directory ${applicationOutputProperties.path}")
        }
        File(applicationOutputProperties.path+"/"+fileName)
            .appendText(homeWizard.getHomeWizardData().toCSV(includingActivePower))
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