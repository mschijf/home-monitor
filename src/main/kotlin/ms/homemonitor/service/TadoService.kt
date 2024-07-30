package ms.homemonitor.service

import ms.homemonitor.infra.tado.rest.Tado
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.TadoRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TadoService(
    private val tado: Tado,
    private val repository: TadoRepository,
    private val measurement: MicroMeterMeasurement
) {

    @Scheduled(fixedRate = 1*60*1000)
    fun tadoMeasurement() {
        val tadoResponse = tado.getTadoResponse()
        repository.storeTadoData(tadoResponse)
        measurement.setMetrics(tadoResponse)
    }
}