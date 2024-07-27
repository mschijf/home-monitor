package ms.powermonitoring.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import ms.powermonitoring.config.ApplicationOutputProperties
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementDataTimed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter


@Service
class Repository(
    private val applicationOutputProperties: ApplicationOutputProperties) {

    private val log = LoggerFactory.getLogger(Repository::class.java)
    private val jsonMapper = ObjectMapper()

    private val hourFileName = applicationOutputProperties.baseFileName+"Hour"
    private val dayFileName = applicationOutputProperties.baseFileName+"Day"
    private val detailedFileName = applicationOutputProperties.baseFileName

    init {
        val resultMkdir: Boolean = File(applicationOutputProperties.path).mkdirs()
        if (resultMkdir) {
            log.info("created the directory ${applicationOutputProperties.path}")
        }
        jsonMapper.registerModule(JavaTimeModule())
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    fun storeDetailedMeasurement(data: HomeWizardMeasurementDataTimed) {
        store(detailedFileName, data, includingActivePower = true)
    }

    fun retrieveLastMeasurementOrNull() : HomeWizardMeasurementDataTimed? =
        retrieveLastMeasurement(detailedFileName)

    fun storeHourlyMeasurement(data: HomeWizardMeasurementDataTimed) {
        store(hourFileName, data, includingActivePower = false)
    }

    fun retrieveLastHourlyMeasurementOrNull() : HomeWizardMeasurementDataTimed? =
        retrieveLastMeasurement(hourFileName)

    fun storeDailyMeasurement(data: HomeWizardMeasurementDataTimed) {
        store(dayFileName, data, includingActivePower = false)
    }

    fun retrieveLastDailyMeasurementOrNull() : HomeWizardMeasurementDataTimed? =
        retrieveLastMeasurement(dayFileName)


    private fun store(fileName: String, data: HomeWizardMeasurementDataTimed, includingActivePower: Boolean) {
        val json = jsonMapper.writeValueAsString(data)
        File(applicationOutputProperties.path+"/"+fileName+"_last.json").writeText(json)
        val textLine = data.toCSV(includingActivePower)
        File(applicationOutputProperties.path+"/"+fileName+".csv").appendText(textLine)
    }

    private fun retrieveLastMeasurement(fileName: String) : HomeWizardMeasurementDataTimed? {
        try {
            return File(applicationOutputProperties.path + "/" + fileName + "_last.json")
                .readText(Charsets.UTF_8)
                .let {
                    jsonMapper.readValue(it, HomeWizardMeasurementDataTimed::class.java)
                }
        } catch(ex: Exception) {
            return null
        }
    }

    //--------------------------------------------------------------------------------------------

    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

    private fun HomeWizardMeasurementDataTimed.toCSV(includingActivePower: Boolean): String {
        val response = this.data
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