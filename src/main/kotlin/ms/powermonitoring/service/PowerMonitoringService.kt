package ms.powermonitoring.service

import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.rest.HomeWizard
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class PowerMonitoringService(
    private val homeWizard: HomeWizard,
    private val repository: Repository,
    private val measurement: MicroMeterMeasurement
) {

    private var lastDetailedMeasurement: HomeWizardMeasurementData = homeWizard.getHomeWizardData()

    @Scheduled(fixedRate = 10_000)
    fun detailedPowerMeasurement() {
        val homeWizardData = homeWizard.getHomeWizardData()
        repository.storeDetailedMeasurement(homeWizardData)
        measurement.setMetrics(homeWizardData)
        lastDetailedMeasurement = homeWizardData
    }

    @Scheduled(cron = "0 0 * * * *")
    fun hourPowerMeasurement() {
        val lastMeasurement = repository.retrieveLastHourlyMeasurementOrNull() ?: lastDetailedMeasurement
        val homeWizardData = homeWizard.getHomeWizardData()
        repository.storeHourlyMeasurement(homeWizardData)

        if (Duration.between(lastMeasurement.time, homeWizardData.time).toSeconds() <= 3600)
            measurement.setHourMetric(homeWizardData, lastMeasurement)
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun dayPowerMeasurement() {
        val lastMeasurement = repository.retrieveLastDailyMeasurementOrNull() ?: lastDetailedMeasurement
        val homeWizardData = homeWizard.getHomeWizardData()
        repository.storeDailyMeasurement(homeWizardData)
        if (Duration.between(lastMeasurement.time, homeWizardData.time).toSeconds() <= 3600*24)
            measurement.setDayMetric(homeWizardData, lastMeasurement)
    }
}