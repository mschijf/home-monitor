package ms.homemonitor.monitor

import io.micrometer.core.instrument.MeterRegistry
import ms.homemonitor.infra.homewizard.model.HomeWizardMeasurementData
import ms.homemonitor.infra.tado.model.TadoResponseModel
import ms.homemonitor.monitor.atomic.wrapper.DoubleWrapper
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

//    setting_heating_power: ON/OFF
//    setting_heating_temperature: ??
//    activityDataPoints_heatingPower_percentage: 0
//    sensorDataPoints_insideTemperature_celsius: 24.24
//    sensorDataPoints_humidity_percentage: 24.24

    private var tadoInsideTemperature = meterRegistry.gauge("tadoInsideTemperature", DoubleWrapper(0.0))!!
    private var tadoHumidityPercentage = meterRegistry.gauge("tadoHumidityPercentage", DoubleWrapper(0.0))!!
    private var tadoHeatingPowerPercentage = meterRegistry.gauge("tadoHeatingPowerPercentage", DoubleWrapper(0.0))!!
    private var tadoOutsideTemperature = meterRegistry.gauge("tadoOutsideTemperature", DoubleWrapper(0.0))!!
    private var tadoOutsideSolarPercentage = meterRegistry.gauge("tadoOutsideSolarPercentage", DoubleWrapper(0.0))!!

    fun setMetrics(lastMeasurement: TadoResponseModel) {
        lastMeasurement.tadoState.let { inside ->
            tadoInsideTemperature.set(inside.sensorDataPoints.insideTemperature.celsius)
            tadoHumidityPercentage.set(inside.sensorDataPoints.humidity.percentage)
            tadoHeatingPowerPercentage.set(inside.activityDataPoints.heatingPower.percentage)
        }
        lastMeasurement.weather.let { outside ->
            tadoOutsideTemperature.set(outside.outsideTemperature.celsius)
            tadoOutsideSolarPercentage.set(outside.solarIntensity.percentage)
        }
    }
}