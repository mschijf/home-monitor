package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.homewizard.model.HomeWizardMeasurementData
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter

@Service
class HomeWizardRepository(
    private val applicationOutputProperties: ApplicationOutputProperties) {

    private val log = LoggerFactory.getLogger(HomeWizardRepository::class.java)

    private val hourFileName = applicationOutputProperties.baseFileName+"Hour"
    private val dayFileName = applicationOutputProperties.baseFileName+"Day"
    private val detailedFileName = applicationOutputProperties.baseFileName

    init {
        val resultMkdir: Boolean = File(applicationOutputProperties.path).mkdirs()
        if (resultMkdir) {
            log.info("created the directory ${applicationOutputProperties.path}")
        }
    }

    fun storeDetailedMeasurement(data: HomeWizardMeasurementData) {
        store(detailedFileName, data, includingActivePower = true)
    }

    fun storeHourlyMeasurement(data: HomeWizardMeasurementData) {
        store(hourFileName, data, includingActivePower = false)
    }

    fun storeDailyMeasurement(data: HomeWizardMeasurementData) {
        store(dayFileName, data, includingActivePower = false)
    }

    private fun store(fileName: String, data: HomeWizardMeasurementData, includingActivePower: Boolean) {
        val textLine = data.toCSV(includingActivePower)
        File(applicationOutputProperties.path + "/" + fileName + ".csv").appendText(textLine)
    }


//    private val jsonMapper = ObjectMapper()
//        jsonMapper.registerModule(JavaTimeModule())
//        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//
//    fun retrieveLastMeasurementOrNull() : HomeWizardMeasurementData? =
//        retrieveLastMeasurement(detailedFileName)
//
//    private fun writeLast(fileName: String, data: HomeWizardMeasurementData) {
//        val json = jsonMapper.writeValueAsString(data)
//        File(applicationOutputProperties.path+"/"+fileName+"_last.json").writeText(json)
//    }
//
//    private fun retrieveLastMeasurement(fileName: String) : HomeWizardMeasurementData? {
//        try {
//            return File(applicationOutputProperties.path + "/" + fileName + "_last.json")
//                .readText(Charsets.UTF_8)
//                .let {
//                    jsonMapper.readValue(it, HomeWizardMeasurementData::class.java)
//                }
//        } catch(ex: Exception) {
//            return null
//        }
//    }
//
    //--------------------------------------------------------------------------------------------

    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

    private fun HomeWizardMeasurementData.toCSV(includingActivePower: Boolean): String {
        val response = this
        var result =  "${this.time.format(timeFormat)};" +
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