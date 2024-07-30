package ms.homemonitor.monitor

import io.micrometer.core.instrument.MeterRegistry
import ms.homemonitor.monitor.atomic.wrapper.DoubleWrapper
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

