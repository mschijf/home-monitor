package ms.homemonitor.smartplug.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.smartplug.repository.SmartPlugRepository
import ms.homemonitor.smartplug.repository.model.SmartPlugEntity
import ms.homemonitor.smartplug.repository.model.SmartPlugId
import ms.homemonitor.smartplug.restclient.TuyaClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class SmartPlugService(
    private val tuyaClient: TuyaClient,
    private val smartPlugRepository: SmartPlugRepository,
) {

    private val zoneId = ZoneId.of("Europe/Berlin")

    fun processMeasurement() {
        //todo: call it every hour(?)
        val deviceList = tuyaClient.getDeviceList()
        deviceList.forEach { deviceId -> processDevice(deviceId) }
    }

    private fun processDevice(deviceId: String) {
        try {
            val lastRecord = smartPlugRepository.getLastSmartPlugEntity(deviceId)
            val startTime = lastRecord?.id?.time?.minusMinutes(1) ?: LocalDate.now().atStartOfDay().minusMinutes(1)
            val endTime = LocalDateTime.now().plusMinutes(1)


            val tuyaDetailList = tuyaClient.getTuyaData(deviceId, startTime, endTime)

            val toBeSaveList = tuyaDetailList.map { tuyaDetail ->
                SmartPlugEntity(
                    SmartPlugId(
                        deviceId,
                        time = LocalDateTime.ofInstant(Instant.ofEpochMilli(tuyaDetail.eventTime), zoneId),
                    ),
                    deltaKWH = BigDecimal(tuyaDetail.value)
                )
            }
            smartPlugRepository.saveAllAndFlush(toBeSaveList)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tuya data", e)
        }
    }
}