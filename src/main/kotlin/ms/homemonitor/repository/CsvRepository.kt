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
        } else {
            log.info("directory $path already exist")
        }
    }

    fun store(fileName: String, csvLine: String) {
        File("$path/$fileName.csv").appendText(csvLine)
    }

}