package ms.homemonitor.controller

import jakarta.transaction.Transactional
import ms.homemonitor.repository.AdminRepository
import ms.homemonitor.repository.PowerRepository
import ms.homemonitor.repository.WaterRepository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RestController
class ControllerTest(
    val adminRepository: AdminRepository,
    private val powerRepository: PowerRepository,
    private val waterRepository: WaterRepository
) {

    private val log = LoggerFactory.getLogger(ControllerTest::class.java)

    @GetMapping("/test")
    @Transactional
    fun getDBRecord(): String {
//        val lastUpdate = adminRepository.findById(0).orElse(AdminEntity(id=0, lastEnecoImport = LocalDateTime.now()))
//        lastUpdate.lastEnecoImport = LocalDateTime.now()
//        adminRepository.saveAndFlush(lastUpdate)
//
//
//        val ld = LocalDateTime.now()
//        val instant = ld.atZone(ZoneId.of("Europe/Amsterdam")).toInstant()
//        return instant.toString()

//        val x = File("data/Export_verbruiken_ENGIE2.csv")
//            .readLines()
//            .drop(1)
//            .map{ PowerImport.of(it) }
//            .filter{ it.type == "Elektriciteit"}
//            .filter{it.time.isAfter(LocalDateTime.of(2023,12,31,23,59, 59))}
//            .sortedBy { it.time }
//            .runningFold(PowerEntity(LocalDateTime.now(), BigDecimal.valueOf(2684.7060), BigDecimal.valueOf(1646))) {
//                acc, powerImport ->
//                if (powerImport.tarief == "Normaal") {
//                    PowerEntity(powerImport.time, acc.powerNormalKwh!! + powerImport.verbruik, acc.powerOffpeakKwh!!)
//                } else {
//                    PowerEntity(powerImport.time, acc.powerNormalKwh!!, acc.powerOffpeakKwh!! + powerImport.verbruik)
//                }
//            }.drop(1)
//        powerRepository.saveAllAndFlush(x)

//        val x = File("data/homeWizardOutputHour.csv")
//            .readLines()
//            .drop(1)
//            .map{ HomeWizardImport.of(it) }
//            .sortedBy { it.time }
//            .map {WaterEntity(it.time, it.water + BigDecimal.valueOf(799.017)) }
//
//        waterRepository.saveAllAndFlush(x)

//        val x = File("data/homeWizardOutputHour.csv")
//            .readLines()
//            .drop(1)
//            .map{ HomeWizardImport.of(it) }
//            .sortedBy { it.time }
//            .map {PowerEntity(it.time, it.powerT2, it.powerT1) }
//
//        powerRepository.saveAllAndFlush(x)

//        val input = listOf<WaterEntity> (
//            WaterEntity(LocalDateTime.parse("2024-01-01T00:00:00"), BigDecimal.valueOf(780.593)),
//            WaterEntity(LocalDateTime.parse("2024-02-04T00:00:00"), BigDecimal.valueOf(781.983)),
//            WaterEntity(LocalDateTime.parse("2024-03-03T00:00:00"), BigDecimal.valueOf(784.777)),
//            WaterEntity(LocalDateTime.parse("2024-04-01T00:00:00"), BigDecimal.valueOf(787.839)),
//            WaterEntity(LocalDateTime.parse("2024-05-01T00:00:00"), BigDecimal.valueOf(790.346)),
//            WaterEntity(LocalDateTime.parse("2024-06-01T00:00:00"), BigDecimal.valueOf(793.095)),
//            WaterEntity(LocalDateTime.parse("2024-07-01T00:00:00"), BigDecimal.valueOf(795.882)),
//            WaterEntity(LocalDateTime.parse("2024-08-01T00:00:00"), BigDecimal.valueOf(798.500)),
//            WaterEntity(LocalDateTime.parse("2024-08-08T00:00:00"), BigDecimal.valueOf(799.017))
//        )
//
//        val updateList = mutableListOf<WaterEntity>()
//        for (i in 0 until input.size-1) {
//            addPeriod(updateList, input[i], input[i+1])
//        }
//        updateList.add(input.last())
//        waterRepository.saveAllAndFlush(updateList)
//
        return "do nothing"
    }

//    private fun addPeriod(updateList: MutableList<WaterEntity>, startEntitiy: WaterEntity, endEntity: WaterEntity) {
//        var currentDay = startEntitiy.time
//        val nDays = ChronoUnit.DAYS.between(startEntitiy.time.toLocalDate(), endEntity.time.toLocalDate())
//        val diff = (endEntity.waterM3!!.toDouble() - startEntitiy.waterM3!!.toDouble())  / nDays
//        repeat (nDays.toInt()) { loopCount ->
//            val newEntity = WaterEntity(currentDay, startEntitiy.waterM3!! + BigDecimal.valueOf(loopCount * diff))
//            updateList.add(newEntity)
//            addEmptyHours(updateList, currentDay, newEntity.waterM3!!)
//            currentDay = currentDay.plusDays(1)
//        }
//    }
//
//    private fun addEmptyHours(updateList: MutableList<WaterEntity>, currentDay: LocalDateTime, value: BigDecimal) {
//        repeat(23) { hour ->
//            updateList.add(WaterEntity(currentDay.plusHours(hour+1L), value))
//        }
//    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////


//    private fun doATadoImport() {
//        var count = 0
//        val listImport = File("./data/tadoTemperature.csv").readLines().drop(1)
//        listImport.forEach { line ->
//            val fields = line.split(";")
//            val tadoEntity = TadoEntity(
//                time = LocalDateTime.parse(fields[0].replace(' ', 'T')),
//                insideTemperature = fields[1].toDouble(),
//                humidityPercentage = fields[2].toDouble(),
//                heatingPowerPercentage = fields[3].toDouble(),
//                settingPowerOn = fields[4] != "OFF",
//                settingTemperature = fields[5].toDouble(),
//                outsideTemperature = fields[6].toDouble(),
//                solarIntensityPercentage = fields[7].toDouble(),
//                weatherState = fields[8]
//            )
//            tadoRepository.save(tadoEntity)
//            count++
//            if (count % 1000 == 0) {
//                println("done $count")
//            }
//        }
//
//        tadoRepository.flush()
//    }

}

data class PowerImport (val time: LocalDateTime, val verbruik: BigDecimal, val type: String, val tarief: String) {
    companion object {
        fun of(importLine: String): PowerImport {
            val fields = importLine.split(",")


            //8-11-2024T5:00:00

            return PowerImport(
                time = LocalDateTime.parse(fields[0].removeSurrounding("\""),
                    DateTimeFormatter.ofPattern("d-M-u H:m:s")),
                verbruik = BigDecimal.valueOf(fields[2].removeSurrounding("\"").toDouble()),
                type = fields[3],
                tarief = fields[4]
            )
        }
    }
}

data class HomeWizardImport (val time: LocalDateTime, val powerT1: BigDecimal, val powerT2: BigDecimal, val water: BigDecimal) {
    companion object {
        fun of(importLine: String): HomeWizardImport {
            val fields = importLine.split(";")

//            time;totalPowerImportT1Kwh;totalPowerImportT2Kwh;totalLiterM3
//            2024-08-08 22:00:00;2158.781;3052.757;0.104

            return HomeWizardImport(
                time = LocalDateTime.parse(fields[0].replace(' ', 'T')),
                powerT1 = BigDecimal.valueOf(fields[1].toDouble()),
                powerT2 = BigDecimal.valueOf(fields[2].toDouble()),
                water = BigDecimal.valueOf(fields[3].toDouble())
            )
        }
    }
}