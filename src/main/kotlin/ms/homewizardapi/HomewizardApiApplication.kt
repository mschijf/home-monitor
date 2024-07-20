package ms.homewizardapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HomewizardApiApplication

fun main(args: Array<String>) {
    runApplication<HomewizardApiApplication>(*args)
}
