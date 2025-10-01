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
        val deviceMasterDataList = tuyaClient.getTuyaDeviceMasterData()
        deviceMasterDataList.forEach { masterData ->
            processDevice(masterData.deviceId, masterData.customName)
        }
    }

    private fun processDevice(deviceId: String, deviceName: String) {
        try {
            val lastRecord = lastRecord(deviceName)

            val startTime = lastRecord.id.time?.plusSeconds(1) ?: LocalDate.now().atStartOfDay().minusMinutes(1)
            val endTime = LocalDateTime.now().plusMinutes(1)

            val tuyaDetailList = tuyaClient.getTuyaData(deviceId, startTime, endTime)
            val toBeSaveList = tuyaDetailList
                .map { tuyaDetail ->
                    SmartPlugEntity(
                        SmartPlugId(
                            name = deviceName,
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

    private fun lastRecord(deviceName: String): SmartPlugEntity {
        return smartPlugRepository
            .getLastSmartPlugEntity(deviceName)
            ?: SmartPlugEntity(
                id = SmartPlugId(
                    name = deviceName,
                    time = LocalDate.now().atStartOfDay().minusMinutes(1)
                ),
                deltaKWH = BigDecimal.ZERO,
            )
    }
}