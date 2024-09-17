package ms.homemonitor.repository

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Repository
import java.io.File
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Repository
class EnecoRepository {
    private val file = File("data/list.eneco.json")
    private val mapper = ObjectMapper()

    private val cache = mutableMapOf<String, List<EnecoDayConsumption>>()

    init {
        mapper.registerModule(JavaTimeModule())
        reloadCache()
    }

    fun store(consumptionList: List<EnecoDayConsumption>): List<EnecoDayConsumption> {
        file.writeText(mapper.writeValueAsString(consumptionList))
        reloadCache()
        return consumptionList
    }

    fun readAll(): List<EnecoDayConsumption> {
        val list =  if (file.exists() && file.length() > 0) {
            val text = file.readText()
            mapper.readValue<List<EnecoDayConsumption>>(text)
        } else {
            emptyList()
        }
        return list
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

data class EnecoDayConsumption(
    @JsonFormat(shape = JsonFormat.Shape.STRING)//, pattern =  "yyyy-MM-ddTHH:mm:ssX")
    @JsonProperty("date")
    val date: LocalDateTime,
    @JsonProperty("totalUsedGigaJoule")
    val totalUsedGigaJoule: BigDecimal
)