package ms.powermonitoring.service

import io.micrometer.core.instrument.MeterRegistry
import ms.powermonitoring.config.ApplicationOutputProperties
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.rest.HomeWizard
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt


@Service
class PowerMonitoringService(
    private val homeWizard: HomeWizard,
    private val applicationOutputProperties: ApplicationOutputProperties,
    meterRegistry: MeterRegistry
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val decimalFormat = DecimalFormat("#0.000")

    fun variableTimedPowerMeasurement() {
        appendToFile(applicationOutputProperties.variableTimeFileName, includingActivePower = true)
        setMetrics(homeWizard.getHomeWizardData())
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

    //--------------------------------------------------------------------------------------------

    private var homewizardPowerImportT1Kwh = meterRegistry.gauge("homewizardPowerImportT1", AtomicInteger(0))!!
    private var homewizardPowerImportT2Kwh = meterRegistry.gauge("homewizardPowerImportT2", AtomicInteger(0))!!
    private var homewizardPowerImportTotalKwh = meterRegistry.gauge("homewizardPowerImportTotalKwh", AtomicInteger(0))!!
    private var homewizardActivePowerL1Watt = meterRegistry.gauge("homewizardActivePowerL1Watt", AtomicInteger(0))!!
    private var homewizardActivePowerL2Watt = meterRegistry.gauge("homewizardActivePowerL2Watt", AtomicInteger(0))!!
    private var homewizardActivePowerL3Watt = meterRegistry.gauge("homewizardActivePowerL3Watt", AtomicInteger(0))!!
    private var homewizardActivePowerTotalWatt = meterRegistry.gauge("homewizardActivePowerTotalWatt", AtomicInteger(0))!!

    private fun setMetrics(lastMeasurement: HomeWizardMeasurementData) {
        homewizardPowerImportTotalKwh.set((lastMeasurement.totalPowerImportKwh.toDouble()*1000.0).roundToInt())
        homewizardPowerImportT1Kwh.set((lastMeasurement.totalPowerImportT1Kwh.toDouble()*1000.0).roundToInt())
        homewizardPowerImportT2Kwh.set((lastMeasurement.totalPowerImportT2Kwh.toDouble()*1000.0).roundToInt())

        homewizardActivePowerTotalWatt.set(lastMeasurement.activePowerWatt.toDouble().roundToInt())
        homewizardActivePowerL1Watt.set(lastMeasurement.activePowerL1Watt.toDouble().roundToInt())
        homewizardActivePowerL2Watt.set(lastMeasurement.activePowerL2Watt.toDouble().roundToInt())
        homewizardActivePowerL3Watt.set(lastMeasurement.activePowerL3Watt.toDouble().roundToInt())
    }

    //--------------------------------------------------------------------------------------------

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