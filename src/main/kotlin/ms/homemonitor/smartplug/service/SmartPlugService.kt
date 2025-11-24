package ms.homemonitor.smartplug.service

import jakarta.transaction.Transactional
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.shared.tools.dateTimeRangeByHour
import ms.homemonitor.smartplug.repository.SmartPlugRepository
import ms.homemonitor.smartplug.repository.SmartPlugStatusRepository
import ms.homemonitor.smartplug.repository.model.SmartPlugEntity
import ms.homemonitor.smartplug.repository.model.SmartPlugId
import ms.homemonitor.smartplug.repository.model.SmartPlugStatusEntity
import ms.homemonitor.smartplug.restclient.TuyaClient
import ms.homemonitor.smartplug.restclient.model.TuyaDeviceMasterData
import org.slf4j.LoggerFactory
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
    private val smartPlugStatusRepository: SmartPlugStatusRepository,
) {

    private val log = LoggerFactory.getLogger(SmartPlugService::class.java)
    private val zoneId = ZoneId.of("Europe/Berlin")

    @Transactional
    fun processMeasurement() {
        processRealDevices()
        processVirtualDevices()
        updateElectricityDetails()
    }

    private fun updateElectricityDetails() {
        smartPlugRepository.refreshElectricityDetail()
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
            val startTime = lastRecordTime(deviceName, deviceId).plusSeconds(1)
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
            saveRecordList(toBeSaveList)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tuya data", e)
        }
    }

    private fun processVirtualDevice(virtualDeviceName: String, wattHourPerHour: BigDecimal) {
        val startTime = lastRecordTime(virtualDeviceName).plusHours(1)
        val endTime = LocalDateTime.now()
        try {
            val toBeSaveList = dateTimeRangeByHour(startTime, endTime)
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
            saveRecordList(toBeSaveList)
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tuya data", e)
        }
    }

    private fun saveRecordList(recordList: List<SmartPlugEntity>) {
        recordList.forEach { smartPlugRecord ->
            try {
                smartPlugRepository.saveAndFlush(smartPlugRecord)
            } catch (e: Exception) {
                log.info("Ignore exception ' ${e.message}' while updating record $smartPlugRecord")
            }
        }
    }

    private fun lastRecordTime(deviceName: String, deviceId: String? = null): LocalDateTime {
        return deviceId?.let { smartPlugRepository.getLastSmartPlugEntityByDeviceId(it)?.id?.time }
            ?: smartPlugRepository.getLastSmartPlugEntityByName(deviceName)?.id?.time
            ?: smartPlugRepository.getLastSmartPlugEntity()?.id?.time
            ?: LocalDate.now().atStartOfDay()
    }
}