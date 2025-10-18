package ms.homemonitor.smartplug.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.dateTimeRangeByHour
import ms.homemonitor.smartplug.repository.SmartPlugRepository
import ms.homemonitor.smartplug.repository.SmartPlugStatusRepository
import ms.homemonitor.smartplug.repository.model.SmartPlugEntity
import ms.homemonitor.smartplug.repository.model.SmartPlugId
import ms.homemonitor.smartplug.repository.model.SmartPlugStatusEntity
import ms.homemonitor.smartplug.restclient.TuyaClient
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.collections.map

@Service
class SmartPlugService(
    private val tuyaClient: TuyaClient,
    private val smartPlugRepository: SmartPlugRepository,
    private val smartPlugStatusRepository: SmartPlugStatusRepository,
) {

    private val zoneId = ZoneId.of("Europe/Berlin")

    fun processSmartPlugStatus() {
        val deviceMasterDataList = tuyaClient.getTuyaDeviceMasterData()
        smartPlugStatusRepository.saveAndFlush(
            SmartPlugStatusEntity(
                time = LocalDateTime.now(),
                numberKnown = deviceMasterDataList.size,
                numberOnLine = deviceMasterDataList.count { it.isOnline },
            )
        )
    }

    fun processMeasurement() {
        processRealDevices()
        processVirtualDevices()
    }

    private fun processRealDevices() {
        val deviceMasterDataList = tuyaClient.getTuyaDeviceMasterData()
        deviceMasterDataList.forEach { masterData ->
            processDevice(masterData.deviceId, masterData.customName)
        }
    }

    private fun processVirtualDevices() {
        VirtualSmartPlug.virtualDeviceList.forEach { virtualDevice ->
            processVirtualDevice(virtualDevice.name, virtualDevice.wattHourPerHour)
        }
    }

    private fun processDevice(deviceId: String, deviceName: String) {
        try {
            val lastRecord = lastRecord(deviceName, isVirtual = false)

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
                        deltaKWH = BigDecimal(tuyaDetail.value),
                        isVirtual = false
                    )
                }
            smartPlugRepository.saveAllAndFlush(toBeSaveList)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tuya data", e)
        }
    }

    private fun processVirtualDevice(virtualDeviceName: String, wattHourPerHour: BigDecimal) {
        val lastRecord = lastRecord(virtualDeviceName, isVirtual = true)
        val startTime = lastRecord.id.time ?: LocalDate.now().atStartOfDay().minusMinutes(1)
        val endTime = LocalDateTime.now()
        try {
            val toBeSaveList = dateTimeRangeByHour(startTime, endTime)
                .drop(1)
                .map { time ->
                    SmartPlugEntity(
                        SmartPlugId(
                            name = virtualDeviceName,
                            time = time,
                        ),
                        deltaKWH = wattHourPerHour,
                        isVirtual = true
                    )
                }.toList()
            smartPlugRepository.saveAllAndFlush(toBeSaveList)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tuya data", e)
        }
    }


    private fun lastRecord(deviceName: String, isVirtual: Boolean): SmartPlugEntity {
        return smartPlugRepository
            .getLastSmartPlugEntity(deviceName)
            ?: SmartPlugEntity(
                id = SmartPlugId(
                    name = deviceName,
                    time = LocalDate.now().atStartOfDay().minusMinutes(1)
                ),
                deltaKWH = BigDecimal.ZERO,
                isVirtual = isVirtual
            )
    }
}