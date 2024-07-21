package ms.powermonitoring

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class PowerMonitoringApplication

fun main(args: Array<String>) {
    runApplication<PowerMonitoringApplication>(*args)
}
