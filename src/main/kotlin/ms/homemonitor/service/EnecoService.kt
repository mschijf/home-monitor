package ms.homemonitor.service

import ms.homemonitor.infra.eneco.rest.Eneco
import ms.homemonitor.monitor.MicroMeterMeasurement
import ms.homemonitor.repository.EnecoDayConsumption
import ms.homemonitor.repository.EnecoRepository
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDate

@Service
class EnecoService(
    private val eneco: Eneco,
    private val enecoRepository: EnecoRepository,
    private val measurement: MicroMeterMeasurement,
) {

    private val initialDate = LocalDate.of(2024, 8, 1)

    fun updateEnecoStatistics(): List<EnecoDayConsumption> {
        val consumptionList = enecoRepository.readAll().sortedBy { it.date }
        val fromDate = consumptionList.lastOrNull()?.date ?: initialDate
        val freshDataList = getNewData(fromDate).sortedBy { it.date }
        updateEnecoStatistics(freshDataList)
//        sendToRaspberryPi(freshDataList)
        return freshDataList
    }

    fun updateEnecoStatistics(source: String): List<EnecoDayConsumption> {
        val consumptionList = enecoRepository.readAll().sortedBy { it.date }
        val fromDate = consumptionList.lastOrNull()?.date ?: initialDate
        val freshDataList = getNewDataBySource(source,fromDate).sortedBy { it.date }
        updateEnecoStatistics(freshDataList)
        return freshDataList
    }

    fun updateEnecoStatistics(freshDataList: List<EnecoDayConsumption>): List<EnecoDayConsumption> {
        val consumptionList = enecoRepository.readAll().sortedBy { it.date }
        enecoRepository.store(consumptionList.dropLast(1 ) + freshDataList)
        recalculatingTotal()
        return freshDataList
    }

    fun recalculatingTotal(): BigDecimal {
        val consumptionList = enecoRepository.readAll()
            .sortedBy { it.date }
        val finalConsumptionSinceInitalDate = consumptionList.sumOf { it.totalUsedGigaJoule }
        setMetrics(finalConsumptionSinceInitalDate)
        return finalConsumptionSinceInitalDate
    }

    private fun getNewData(fromDate: LocalDate): List<EnecoDayConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoDataByScraping(fromDate, now.plusDays(1))!!
        return response.data.usages[0].entries
            .map{ EnecoDayConsumption(it.actual.date.toLocalDate(), it.actual.warmth.high)}
    }

    private fun getNewDataBySource(source: String, fromDate: LocalDate): List<EnecoDayConsumption> {
        val now = LocalDate.now()
        val response = eneco.getEnecoDataBySourcePage(source, fromDate, now.plusDays(1))!!
        return response.data.usages[0].entries
            .map{ EnecoDayConsumption(it.actual.date.toLocalDate(), it.actual.warmth.high)}
    }


    private fun setMetrics(finalValue: BigDecimal) {
        measurement.setDoubleGauge("warmthStandingGJ", finalValue.toDouble())
    }

    private fun sendToRaspberryPi(freshData: List<EnecoDayConsumption>) {
        val restTemplate = RestTemplate()

        val headers = org.springframework.http.HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        val request = HttpEntity<List<EnecoDayConsumption>>(freshData, headers)

        restTemplate.postForObject("http://192.168.2.39:8080/eneco-data-by-list", request, String::class.java)
    }
}