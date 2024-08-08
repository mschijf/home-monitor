package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.homewizard.model.HomeWizardEnergyData
import org.springframework.stereotype.Service
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@Service
class HomeWizardRepository (
    applicationOutputProperties: ApplicationOutputProperties): CsvRepository(applicationOutputProperties) {

    private val baseFileName = "smartMeterOutput"

    fun storeDetailedMeasurement(data: HomeWizardEnergyData) {
        store(baseFileName, data.toCSV())
    }

    fun storeHourlyMeasurement(data: HomeWizardEnergyData) {
        store(baseFileName+"Hour", data.toCSV())
    }

    fun storeDailyMeasurement(data: HomeWizardEnergyData) {
        store(baseFileName+"Day", data.toCSV())
    }


    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

    fun HomeWizardEnergyData.toCSV(): String {
        return "${this.time.format(timeFormat)};" +
                "${decimalFormat.format(this.totalPowerImportT1Kwh)};" +
                "${decimalFormat.format(this.totalPowerImportT2Kwh)};\n"
    }

}