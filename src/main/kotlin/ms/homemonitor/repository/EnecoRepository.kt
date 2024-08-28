package ms.homemonitor.repository

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Repository
import java.io.File
import java.math.BigDecimal
import java.time.LocalDate

@Repository
class EnecoRepository {
    private val file = File("data/list.eneco.json")
    private val mapper = ObjectMapper()

    init {
        mapper.registerModule(JavaTimeModule())
    }

    fun store(consumptionList: List<EnecoDayConsumption>): List<EnecoDayConsumption> {
        file.writeText(mapper.writeValueAsString(consumptionList))
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

}

data class EnecoDayConsumption(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  "yyyy-MM-dd")
    @JsonProperty("date")
    val date: LocalDate,
    @JsonProperty("totalUsedGigaJoule")
    val totalUsedGigaJoule: BigDecimal
)