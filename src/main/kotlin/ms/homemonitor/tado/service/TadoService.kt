package ms.homemonitor.tado.service

import jakarta.transaction.Transactional
import ms.homemonitor.shared.HomeMonitorException
import ms.homemonitor.tado.repository.TadoDeviceRepository
import ms.homemonitor.tado.repository.TadoRepository
import ms.homemonitor.tado.repository.model.TadoDeviceEntity
import ms.homemonitor.tado.repository.model.TadoEntity
import ms.homemonitor.tado.restclient.TadoClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class TadoService(
    private val tadoClient: TadoClient,
    private val tadoRepository: TadoRepository,
    private val tadoDeviceRepository: TadoDeviceRepository,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun processMeasurement() {
        try {
            val now = LocalDateTime.now()
            val tadoResponse = tadoClient.getTadoResponse()
            tadoRepository.saveAndFlush(
                TadoEntity(
                    time = now,
                    insideTemperature = tadoResponse.tadoState.sensorDataPoints.insideTemperature.celsius,
                    humidityPercentage = tadoResponse.tadoState.sensorDataPoints.humidity.percentage,
                    heatingPowerPercentage = tadoResponse.tadoState.activityDataPoints.heatingPower.percentage,
                    settingPowerOn = tadoResponse.tadoState.setting.power == "ON",
                    settingTemperature = tadoResponse.tadoState.setting.temperature?.celsius ?: 0.0,
                    outsideTemperature = tadoResponse.weather.outsideTemperature.celsius,
                    solarIntensityPercentage = tadoResponse.weather.solarIntensity.percentage,
                    weatherState = tadoResponse.weather.weatherState.value,
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
    }

    fun processDeviceInfo() {
        val now = LocalDateTime.now()
        val deviceInfo = tadoClient.getTadoDeviceInfo()
        tadoDeviceRepository.saveAndFlush(
            TadoDeviceEntity(
                time=now,
                batteryState = deviceInfo.batteryState
            )
        )
        println("Battery Info: ${deviceInfo.batteryState}")
    }

}