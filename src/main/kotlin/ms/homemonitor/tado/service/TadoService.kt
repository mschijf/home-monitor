package ms.homemonitor.tado.service

import jakarta.transaction.Transactional
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.tado.repository.TadoDeviceStateRepository
import ms.homemonitor.tado.repository.TadoOutsideRepository
import ms.homemonitor.tado.repository.TadoRepository
import ms.homemonitor.tado.repository.model.TadoDeviceStateEntity
import ms.homemonitor.tado.repository.model.TadoEntity
import ms.homemonitor.tado.repository.model.TadoOutsideEntity
import ms.homemonitor.tado.restclient.TadoClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TadoService(
    private val tadoClient: TadoClient,
    private val tadoRepository: TadoRepository,
    private val tadoOutsideRepository: TadoOutsideRepository,
    private val tadoDeviceStateRepository: TadoDeviceStateRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun processMeasurement() {
        try {
            val now = LocalDateTime.now()
            tadoClient.getAllZones().zoneStates.forEach { (zoneId, tadoInside) ->
                tadoRepository.saveAndFlush(
                    TadoEntity(
                        time = now,
                        zoneId = zoneId.toInt(),
                        insideTemperature = tadoInside.sensorDataPoints.insideTemperature.celsius,
                        humidityPercentage = tadoInside.sensorDataPoints.humidity.percentage,
                        heatingPowerPercentage = tadoInside.activityDataPoints.heatingPower.percentage,
                        settingPowerOn = tadoInside.setting.power == "ON",
                        settingTemperature = tadoInside.setting.temperature?.celsius ?: 0.0,
                    )
                )
            }
            val tadoOutside = tadoClient.getTadoOutsideWeather()
            tadoOutsideRepository.saveAndFlush(
                TadoOutsideEntity(
                    time = now,
                    outsideTemperature = tadoOutside.outsideTemperature.celsius,
                    solarIntensityPercentage = tadoOutside.solarIntensity.percentage,
                )
            )

        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing Tado data", e)
        }
    }

    @Transactional
    fun cleanupOldData(keepDays: Long) {
        val beforeTime = LocalDate.now().minusDays(keepDays)

        val recordsToDelete = tadoRepository.countRecordsBeforeTime(beforeTime.atStartOfDay())
        tadoRepository.deleteDataBeforeTime(beforeTime.atStartOfDay())
        log.info("Deleted $recordsToDelete tado records")

        val outsideRecordsToDelete = tadoOutsideRepository.countRecordsBeforeTime(beforeTime.atStartOfDay())
        tadoOutsideRepository.deleteDataBeforeTime(beforeTime.atStartOfDay())
        log.info("Deleted $outsideRecordsToDelete tado_outside records")
    }

    fun processDeviceInfo() {
        tadoClient.getTadoZones().forEach { zone ->
            zone.deviceList.forEach { device ->
                tadoDeviceStateRepository.saveAndFlush(
                    TadoDeviceStateEntity(
                        serialNumber = device.serialNo,
                        zoneId = zone.id,
                        zoneName = zone.name,
                        batteryState = device.batteryState,
                    )
                )
            }
        }
    }

}