package ms.homemonitor.monitor

import io.micrometer.core.instrument.MeterRegistry
import ms.homemonitor.infra.homewizard.model.HomeWizardMeasurementData
import ms.homemonitor.infra.tado.model.TadoState
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt

@Service
class MicroMeterMeasurement(
    meterRegistry: MeterRegistry) {

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

    fun setMetrics(lastMeasurement: TadoState) {
    }

}