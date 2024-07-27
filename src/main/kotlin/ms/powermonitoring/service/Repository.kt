package ms.powermonitoring.service

import ms.powermonitoring.config.ApplicationOutputProperties
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class Repository(
    private val applicationOutputProperties: ApplicationOutputProperties) {

    private val log = LoggerFactory.getLogger(Repository::class.java)

    fun appendToDetailedFile(data: HomeWizardMeasurementData) {
        appendToFile(applicationOutputProperties.variableTimeFileName, data, includingActivePower = true)
    }

    fun appendToHourFile(data: HomeWizardMeasurementData) {
        appendToFile(applicationOutputProperties.hourFileName, data, includingActivePower = false)
    }

    fun appendToDayFile(data: HomeWizardMeasurementData) {
        appendToFile(applicationOutputProperties.dayFileName, data, includingActivePower = false)
    }

    private fun appendToFile(fileName: String, data: HomeWizardMeasurementData, includingActivePower: Boolean) {
        val textLine = data.toCSV(includingActivePower)
        val resultMkdirs: Boolean = File(applicationOutputProperties.path).mkdirs()
        if (resultMkdirs) {
            log.info("created the directory ${applicationOutputProperties.path}")
        }
        File(applicationOutputProperties.path+"/"+fileName).appendText(textLine)
    }

    //--------------------------------------------------------------------------------------------

    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

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