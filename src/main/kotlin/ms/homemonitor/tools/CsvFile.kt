package ms.homemonitor.tools

import java.io.File

class CsvFile(
    private val path: String,
    private val fileName: String,
    private val header: String = "") {

    private val file = File("$path/$fileName")

    init {
        File(path).mkdirs()
    }

    fun clearFile() {
        File(path).mkdirs()
        if (header.isNotEmpty()) {
            file.writeText(header + "\n")
        } else {
            file.writeText("")
        }
    }

    fun append(csvLine: String) {
        if (!file.exists() || file.length() == 0L){
            clearFile()
        }
        file.appendText(csvLine+"\n")
    }

    fun readCsvLines(): List<List<String>> {
        return if (file.exists()) {
            if (header.isEmpty()) {
                file.readLines().map { line -> line.split(';') }
            } else {
                file.readLines().map { line -> line.split(';') }.drop(1)
            }
        } else {
            emptyList()
        }
    }

}