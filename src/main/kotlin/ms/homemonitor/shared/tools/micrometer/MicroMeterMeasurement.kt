package ms.homemonitor.shared.tools.micrometer

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

@Service
class MicroMeterMeasurement(
    private val meterRegistry: MeterRegistry) {

    private val meterMap = mutableMapOf<String, DoubleWrapper>()
    private val counterMap = mutableMapOf<String, Counter>()

    fun setDoubleGauge(meterId: String, value: Double) {
        meterMap
            .getOrPut(meterId) { meterRegistry.gauge(meterId, DoubleWrapper(value))!! }
            .set(value)
    }

    fun increaseCounter(meterId: String) {
        counterMap
            .getOrPut(meterId) { meterRegistry.counter(meterId) }
            .increment()
    }
}

