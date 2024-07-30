package ms.homemonitor.monitor

import io.micrometer.core.instrument.MeterRegistry
import ms.homemonitor.infra.homewizard.model.HomeWizardMeasurementData
import ms.homemonitor.infra.tado.model.TadoResponseModel
import ms.homemonitor.monitor.atomic.wrapper.DoubleWrapper
import org.springframework.stereotype.Service

@Service
class MicroMeterMeasurement(
    meterRegistry: MeterRegistry) {

    private var homewizardPowerImportT1Kwh = meterRegistry.gauge("homewizardPowerT1Kwh", DoubleWrapper(0.0))!!
    private var homewizardPowerImportT2Kwh = meterRegistry.gauge("homewizardPowerT2Kwh", DoubleWrapper(0.0))!!
    private var homewizardActivePowerL1Watt = meterRegistry.gauge("homewizardActivePowerL1Watt", DoubleWrapper(0.0))!!
    private var homewizardActivePowerL2Watt = meterRegistry.gauge("homewizardActivePowerL2Watt", DoubleWrapper(0.0))!!
    private var homewizardActivePowerL3Watt = meterRegistry.gauge("homewizardActivePowerL3Watt", DoubleWrapper(0.0))!!

    fun setMetrics(lastMeasurement: HomeWizardMeasurementData) {
        homewizardPowerImportT1Kwh.set(lastMeasurement.totalPowerImportT1Kwh.toDouble())
        homewizardPowerImportT2Kwh.set(lastMeasurement.totalPowerImportT2Kwh.toDouble())

        homewizardActivePowerL1Watt.set(lastMeasurement.activePowerL1Watt.toDouble())
        homewizardActivePowerL2Watt.set(lastMeasurement.activePowerL2Watt.toDouble())
        homewizardActivePowerL3Watt.set(lastMeasurement.activePowerL3Watt.toDouble())
    }

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