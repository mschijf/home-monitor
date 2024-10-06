package ms.homemonitor.micrometer

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

@Service
class MicroMeterMeasurement(
    private val meterRegistry: MeterRegistry) {

    private val meterMap = mutableMapOf<String, DoubleWrapper>()

    fun setDoubleGauge(meterId: String, value: Double) {
        meterMap
            .getOrPut(meterId) { meterRegistry.gauge(meterId, DoubleWrapper(value))!! }
            .set(value)
    }
}

