package ms.powermonitoring.homewizard.rest

import ms.powermonitoring.homewizard.model.HomeWizardMeasurementData
import ms.powermonitoring.homewizard.model.HomeWizardMeasurementDataTimed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime


@Service
class HomeWizard {
    private val baseUrl = "http://192.168.2.40"
    private val restTemplate = RestTemplate()

    fun getHomeWizardData(): HomeWizardMeasurementDataTimed {
        val response = restTemplate
            .getForObject("$baseUrl/api/v1/data", HomeWizardMeasurementData::class.java)
            ?: throw IllegalStateException("Could not get data from HomeWizard. - response is null")

        return HomeWizardMeasurementDataTimed(LocalDateTime.now(), response)
    }

}


