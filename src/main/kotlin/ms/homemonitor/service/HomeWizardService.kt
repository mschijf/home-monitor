package ms.homemonitor.service

import ms.homemonitor.infra.homewizard.rest.HomeWizard
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.HomeWizardRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class HomeWizardService(
    private val homeWizard: HomeWizard,
    private val repository: HomeWizardRepository,
    private val measurement: MicroMeterMeasurement
) {

    @Scheduled(fixedRate = 10_000)
    fun detailedPowerMeasurement() {
        val homeWizardData = homeWizard.getHomeWizardData()
        repository.storeDetailedMeasurement(homeWizardData)
        measurement.setMetrics(homeWizardData)
    }

    @Scheduled(cron = "0 0 * * * *")
    fun hourPowerMeasurement() {
        val homeWizardData = homeWizard.getHomeWizardData()
        repository.storeHourlyMeasurement(homeWizardData)
    }

    @Scheduled(cron = "0 0 0 * * *")
    fun dayPowerMeasurement() {
        val homeWizardData = homeWizard.getHomeWizardData()
        repository.storeDailyMeasurement(homeWizardData)
    }
}