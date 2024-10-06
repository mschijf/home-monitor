package ms.homemonitor.domain.eneco.repository

import ms.homemonitor.tools.CsvFile
import ms.homemonitor.DataOutputProperties
import ms.homemonitor.domain.eneco.model.EnecoConsumption
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class EnecoRepository(dataOutputProperties: DataOutputProperties) {

    private val enecoCsvFile = CsvFile(
        path = dataOutputProperties.path,
        fileName = "eneco.csv",
        header = "time;totalUsedGigaJoule"
    )
    private val cache = mutableMapOf<String, List<EnecoConsumption>>()

    private fun EnecoConsumption.toCSV(): String {
        return "${this.date};" +
                "${this.totalUsedGigaJoule}"
    }

    fun storeEnecoData(consumptionList: List<EnecoConsumption>): List<EnecoConsumption> {
        enecoCsvFile.clearFile()
        consumptionList.forEach { consumption ->
            enecoCsvFile.append(consumption.toCSV())
        }

        cache.clear()
        return consumptionList
    }

    fun readAll(): List<EnecoConsumption> {
        return enecoCsvFile
            .readCsvLines()
            .map { EnecoConsumption(LocalDateTime.parse(it[0]), BigDecimal(it[1])) }
    }

    fun getHourList(): List<EnecoConsumption> {
        return cache.getOrPut("hour") { readAll() }
    }

    fun getDayList(): List<EnecoConsumption> {
        return cache.getOrPut("day") {
            getHourList()
                .groupBy { it.date.toLocalDate() }
                .mapValues { it.value.sumOf { e -> e.totalUsedGigaJoule } }
                .map{ EnecoConsumption(LocalDateTime.of(it.key, LocalTime.of(0,0,0)), it.value) }
        }
    }
}