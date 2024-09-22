package ms.homemonitor.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.eneco.model.EnecoDayConsumption
import org.springframework.stereotype.Repository
import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class EnecoRepository(
    applicationOutputProperties: ApplicationOutputProperties): CsvRepository(applicationOutputProperties) {

    private val file = File("data/list.eneco.json")

    private val enecoCsvFileName = "eneco"
    private val csvHeader = "time;totalUsedGigaJoule\n"

    private val mapper = ObjectMapper()

    private val cache = mutableMapOf<String, List<EnecoDayConsumption>>()

    init {
        mapper.registerModule(JavaTimeModule())
        reloadCache()
    }

    private fun EnecoDayConsumption.toCSV(): String {
        return "${this.date};" +
                "${this.totalUsedGigaJoule}\n"
    }

    fun store(consumptionList: List<EnecoDayConsumption>): List<EnecoDayConsumption> {
//        file.writeText(mapper.writeValueAsString(consumptionList))
        consumptionList.forEach { consumption ->
            super.store(enecoCsvFileName,consumption.toCSV(), csvHeader)
        }

        reloadCache()
        return consumptionList
    }

    fun readAll(): List<EnecoDayConsumption> {
        val file2 = File("data/$enecoCsvFileName.csv")
        if (!file2.exists()) {
            //migrate
            val list =  if (file.exists() && file.length() > 0) {
                val text = file.readText()
                mapper.readValue<List<EnecoDayConsumption>>(text)
            } else {
                emptyList()
            }
            store(list)
        }

        val list2 =  if (file2.exists() && file2.length() > 0) {
            super
                .readCsvLines(enecoCsvFileName)
                .drop(1)
                .map {
                    EnecoDayConsumption(LocalDateTime.parse(it[0]), BigDecimal(it[1]))
                }
        } else {
            emptyList()
        }
        return list2
//
//        val list =  if (file.exists() && file.length() > 0) {
//            val text = file.readText()
//            mapper.readValue<List<EnecoDayConsumption>>(text)
//        } else {
//            emptyList()
//        }
//
//        return list
//
    }


    private fun reloadCache() {
        cache.clear()
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