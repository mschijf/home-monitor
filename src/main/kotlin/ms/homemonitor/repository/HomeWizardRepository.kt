package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.homewizard.model.HomeWizardData
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@Service
class HomeWizardRepository (
    applicationOutputProperties: ApplicationOutputProperties): CsvRepository(applicationOutputProperties) {

    private val baseFileName = "homeWizardOutput"

    fun storeDetailedMeasurement(data: HomeWizardData) {
        store(baseFileName, data.toCSV(), csvHeader)
    }

    fun storeHourlyMeasurement(data: HomeWizardData) {
        store(baseFileName+"Hour", data.toCSV(), csvHeader)
    }

    fun storeDailyMeasurement(data: HomeWizardData) {
        store(baseFileName+"Day", data.toCSV(), csvHeader)
    }


    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

    private val csvHeader = "time;totalPowerImportT1Kwh;totalPowerImportT2Kwh;totalLiterM3\n"

    fun HomeWizardData.toCSV(): String {
        return "${this.energy.time.format(timeFormat)};" +
                "${decimalFormat.format(this.energy.totalPowerImportT1Kwh)};" +
                "${decimalFormat.format(this.energy.totalPowerImportT2Kwh)};" +
                "${decimalFormat.format(this.water.totalLiterM3)}\n"
    }

}