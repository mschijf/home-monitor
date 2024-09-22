package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import org.slf4j.LoggerFactory
import java.io.File

abstract class CsvRepository(applicationOutputProperties: ApplicationOutputProperties) {

    private val log = LoggerFactory.getLogger(CsvRepository::class.java)
    private val path = applicationOutputProperties.path

    init {
        if (File(path).mkdirs()) {
            log.info("created the directory $path")
        }
    }

    fun store(fileName: String, csvLine: String, csvHeader: String = "") {
        val file = File("$path/$fileName.csv")
        if (!file.exists() || file.length() == 0L) {
            file.appendText(csvHeader)
        }
        file.appendText(csvLine)
    }

    fun readCsvLines(fileName: String): List<List<String>> {
        val file = File("$path/$fileName.csv")
        return if (file.exists()) {
            file.readLines().map { line -> line.split(';') }
        } else {
            emptyList()
        }
    }

}