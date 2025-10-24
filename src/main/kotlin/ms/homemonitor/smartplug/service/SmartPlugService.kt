package ms.homemonitor.smartplug.service

import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.dateTimeRangeByHour
import ms.homemonitor.smartplug.repository.SmartPlugRepository
import ms.homemonitor.smartplug.repository.SmartPlugStatusRepository
import ms.homemonitor.smartplug.repository.model.SmartPlugEntity
import ms.homemonitor.smartplug.repository.model.SmartPlugId
import ms.homemonitor.smartplug.repository.model.SmartPlugStatusEntity
import ms.homemonitor.smartplug.restclient.TuyaClient
import ms.homemonitor.smartplug.restclient.model.TuyaDeviceMasterData
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class SmartPlugService(
    private val tuyaClient: TuyaClient,
    private val smartPlugRepository: SmartPlugRepository,
    private val smartPlugStatusRepository: SmartPlugStatusRepository,
) {

    private val zoneId = ZoneId.of("Europe/Berlin")

    fun processMeasurement() {
        processRealDevices()
        processVirtualDevices()
    }

    private fun processRealDevices() {
        val deviceMasterDataList = tuyaClient.getTuyaDeviceMasterData()
        processSmartPlugStatus(deviceMasterDataList)
        deviceMasterDataList.forEach { masterData ->
            processDevice(masterData.deviceId, masterData.customName)
        }
    }

    private fun processSmartPlugStatus(deviceMasterDataList: List<TuyaDeviceMasterData>) {
        smartPlugStatusRepository.saveAndFlush(
            SmartPlugStatusEntity(
                time = LocalDateTime.now(),
                numberKnown = deviceMasterDataList.size,
                numberOnLine = deviceMasterDataList.count { it.isOnline },
            )
        )
    }

    private fun processVirtualDevices() {
        VirtualSmartPlug.virtualDeviceList.forEach { virtualDevice ->
            processVirtualDevice(virtualDevice.name, virtualDevice.wattHourPerHour)
        }
    }

    private fun processDevice(deviceId: String, deviceName: String) {
        try {
            val startTime = lastRecordTime(deviceName)
            val endTime = LocalDateTime.now()

            val tuyaDetailList = tuyaClient.getTuyaData(deviceId, startTime, endTime)
            val toBeSaveList = tuyaDetailList
                .map { tuyaDetail ->
                    SmartPlugEntity(
                        SmartPlugId(
                            name = deviceName,
                            time = LocalDateTime.ofInstant(Instant.ofEpochMilli(tuyaDetail.eventTime), zoneId),
                        ),
                        deltaWH = BigDecimal(tuyaDetail.value),
                        deviceId = deviceId
                    )
                }
            smartPlugRepository.saveAllAndFlush(toBeSaveList)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tuya data", e)
        }
    }

    private fun processVirtualDevice(virtualDeviceName: String, wattHourPerHour: BigDecimal) {
        val startTime = lastRecordTime(virtualDeviceName)
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
                        deltaWH = wattHourPerHour,
                        deviceId = null
                    )
                }.toList()
            smartPlugRepository.saveAllAndFlush(toBeSaveList)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tuya data", e)
        }
    }


    private fun lastRecordTime(deviceName: String): LocalDateTime {
        return smartPlugRepository.getLastSmartPlugEntityByName(deviceName)?.id?.time
            ?: smartPlugRepository.getLastSmartPlugEntity()?.id?.time?.plusSeconds(1)
            ?: LocalDateTime.now()
    }
}