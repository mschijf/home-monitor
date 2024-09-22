package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.eneco.model.EnecoDayConsumption
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class EnecoRepository(applicationOutputProperties: ApplicationOutputProperties) {

    private val enecoCsvFile = CsvFile(
        path = applicationOutputProperties.path,
        fileName = "eneco.csv",
        header = "time;totalUsedGigaJoule"
    )
    private val cache = mutableMapOf<String, List<EnecoDayConsumption>>()

    private fun EnecoDayConsumption.toCSV(): String {
        return "${this.date};" +
                "${this.totalUsedGigaJoule}\n"
    }

    fun storeEnecoData(consumptionList: List<EnecoDayConsumption>): List<EnecoDayConsumption> {
        enecoCsvFile.clearFile()
        consumptionList.forEach { consumption ->
            enecoCsvFile.append(consumption.toCSV())
        }

        cache.clear()
        return consumptionList
    }

    fun readAll(): List<EnecoDayConsumption> {
        return enecoCsvFile
            .readCsvLines()
            .map { EnecoDayConsumption(LocalDateTime.parse(it[0]), BigDecimal(it[1])) }
    }

    fun getHourList(): List<EnecoDayConsumption> {
        return cache.getOrPut("hour") { readAll() }
    }

    fun getDayList(): List<EnecoDayConsumption> {
        return cache.getOrPut("day") {
            getHourList()
                .groupBy { it.date.toLocalDate() }
                .mapValues { it.value.sumOf { e -> e.totalUsedGigaJoule } }
                .map{ EnecoDayConsumption(LocalDateTime.of(it.key, LocalTime.of(0,0,0)), it.value) }
        }
    }
}