package ms.homemonitor.service

import ms.homemonitor.config.WeerLiveProperties
import ms.homemonitor.infra.weerlive.model.WeerLiveModel
import ms.homemonitor.infra.weerlive.rest.WeerLive
import ms.homemonitor.monitor.MicroMeterMeasurement
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class WeerLiveService(
    private val weerLive: WeerLive,
    private val measurement: MicroMeterMeasurement,
    private val weerLiveProperties: WeerLiveProperties
) {

    private val log = LoggerFactory.getLogger(WeerLiveService::class.java)

    @Scheduled(cron = "0 */10 * * * *")
    fun weerLiveMeasurement() {
        if (!weerLiveProperties.enabled)
            return
        val weerLiveModel = weerLive.getWeerLiveData()
        setMetrics(weerLiveModel)
        if (weerLiveModel.api[0].numberOfRequestsLeft == 10)
            log.warn("Requests left smaller then 10: Retrieving weerlivemodel with time: ${weerLiveModel.currentWeather[0].time}, requests left: ${weerLiveModel.api[0].numberOfRequestsLeft}")
    }

    fun setMetrics(data: WeerLiveModel) {
        measurement.setDoubleGauge("weerLiveOutsideTemperature", data.currentWeather[0].temp)
    }
}