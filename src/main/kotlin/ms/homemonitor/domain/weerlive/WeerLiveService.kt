package ms.homemonitor.domain.weerlive

import ms.homemonitor.application.HomeMonitorException
import ms.homemonitor.domain.weerlive.model.WeerLiveModel
import ms.homemonitor.domain.weerlive.rest.WeerLive
import ms.homemonitor.micrometer.MicroMeterMeasurement
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class WeerLiveService(
    private val weerLive: WeerLive,
    private val measurement: MicroMeterMeasurement,
    @Value("\${weerlive.enabled}") private val enabled: Boolean,
) {

    private val log = LoggerFactory.getLogger(WeerLiveService::class.java)

    @Scheduled(cron = "0 */10 * * * *")
    fun weerLiveMeasurement() {
        if (!enabled)
            return

        try {
            val weerLiveModel = weerLive.getWeerLiveData()
            if (weerLiveModel != null) {
                setMetrics(weerLiveModel)
                if (weerLiveModel.api[0].numberOfRequestsLeft == 10)
                    log.warn("Requests left smaller then 10: Retrieving weerlivemodel with time: ${weerLiveModel.currentWeather[0].time}, requests left: ${weerLiveModel.api[0].numberOfRequestsLeft}")
            }
        } catch (e: Exception) {
            throw HomeMonitorException("Error while processing WeerLive data", e)
        }
    }

    fun setMetrics(data: WeerLiveModel) {
        measurement.setDoubleGauge("weerLiveOutsideTemperature", data.currentWeather[0].temp)
    }
}