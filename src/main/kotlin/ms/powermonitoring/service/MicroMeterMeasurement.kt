package ms.powermonitoring.service

import io.micrometer.core.instrument.MeterRegistry
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

@Service
class MicroMeterMeasurement(
    meterRegistry: MeterRegistry) {

    private var homewizardPowerT1ConsumedHourKwh = meterRegistry.gauge("homewizardPowerT1ConsumedHourKwh", AtomicInteger(0))!!
    private var homewizardPowerT2ConsumedHourKwh = meterRegistry.gauge("homewizardPowerT2ConsumedHourKwh", AtomicInteger(0))!!
    private var homewizardPowerT1ConsumedDayKwh = meterRegistry.gauge("homewizardPowerT1ConsumedDayKwh", AtomicInteger(0))!!
    private var homewizardPowerT2ConsumedDayKwh = meterRegistry.gauge("homewizardPowerT2ConsumedDayKwh", AtomicInteger(0))!!

    private var homewizardPowerImportT1Kwh = meterRegistry.gauge("homewizardPowerImportT1", AtomicInteger(0))!!
    private var homewizardPowerImportT2Kwh = meterRegistry.gauge("homewizardPowerImportT2", AtomicInteger(0))!!
    private var homewizardActivePowerL1Watt = meterRegistry.gauge("homewizardActivePowerL1Watt", AtomicInteger(0))!!
    private var homewizardActivePowerL2Watt = meterRegistry.gauge("homewizardActivePowerL2Watt", AtomicInteger(0))!!
    private var homewizardActivePowerL3Watt = meterRegistry.gauge("homewizardActivePowerL3Watt", AtomicInteger(0))!!

    fun setMetrics(lastMeasurement: HomeWizardMeasurementData) {
        homewizardPowerImportT1Kwh.set((lastMeasurement.totalPowerImportT1Kwh.toDouble()*1000.0).roundToInt())
        homewizardPowerImportT2Kwh.set((lastMeasurement.totalPowerImportT2Kwh.toDouble()*1000.0).roundToInt())

        homewizardActivePowerL1Watt.set(lastMeasurement.activePowerL1Watt.toDouble().roundToInt())
        homewizardActivePowerL2Watt.set(lastMeasurement.activePowerL2Watt.toDouble().roundToInt())
        homewizardActivePowerL3Watt.set(lastMeasurement.activePowerL3Watt.toDouble().roundToInt())
    }

    fun setHourMetric(current: HomeWizardMeasurementData, previous: HomeWizardMeasurementData) {
        homewizardPowerT1ConsumedHourKwh.set(current.totalPowerImportT1Kwh.diffInInt(previous.totalPowerImportT1Kwh))
        homewizardPowerT2ConsumedHourKwh.set(current.totalPowerImportT1Kwh.diffInInt(previous.totalPowerImportT1Kwh))
    }

    fun setDayMetric(current: HomeWizardMeasurementData, previous: HomeWizardMeasurementData) {
        homewizardPowerT1ConsumedDayKwh.set(current.totalPowerImportT1Kwh.diffInInt(previous.totalPowerImportT1Kwh))
        homewizardPowerT2ConsumedDayKwh.set(current.totalPowerImportT1Kwh.diffInInt(previous.totalPowerImportT1Kwh))
    }

    private fun BigDecimal.diffInInt(other: BigDecimal):Int {
        return (this.toDouble() - other.toDouble()).roundToInt()
    }

}