package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.tado.model.TadoState
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class TadoRepository(
    private val applicationOutputProperties: ApplicationOutputProperties) {

    private val log = LoggerFactory.getLogger(TadoRepository::class.java)

    fun storeTadoData(data: TadoState) {
//        store(dayFileName, data, includingActivePower = false)
    }

}