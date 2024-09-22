package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.homewizard.model.HomeWizardData
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@Service
class HomeWizardRepository (
    applicationOutputProperties: ApplicationOutputProperties) {

    private val csvHeader = "time;totalPowerImportT1Kwh;totalPowerImportT2Kwh;totalLiterM3\n"
    private val detailedCsvFile = CsvFile(path = applicationOutputProperties.path, fileName = "homeWizardOutput.csv", header = csvHeader)
    private val hourCsvFile = CsvFile(path = applicationOutputProperties.path, fileName = "homeWizardOutputHour.csv", header = csvHeader)
    private val dayCsvFile = CsvFile(path = applicationOutputProperties.path, fileName = "homeWizardOutputDay.csv", header = csvHeader)

    fun storeDetailedMeasurement(data: HomeWizardData) {
        detailedCsvFile.append(data.toCSV())
    }

    fun storeHourlyMeasurement(data: HomeWizardData) {
        hourCsvFile.append(data.toCSV())
    }

    fun storeDailyMeasurement(data: HomeWizardData) {
        dayCsvFile.append(data.toCSV())
    }


    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

    fun HomeWizardData.toCSV(): String {
        return "${this.energy.time.format(timeFormat)};" +
                "${decimalFormat.format(this.energy.totalPowerImportT1Kwh)};" +
                "${decimalFormat.format(this.energy.totalPowerImportT2Kwh)};" +
                "${decimalFormat.format(this.water.totalLiterM3)}"
    }

}