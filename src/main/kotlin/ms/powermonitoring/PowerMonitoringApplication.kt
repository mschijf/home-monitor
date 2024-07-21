package ms.powermonitoring

import ms.powermonitoring.config.ApplicationOutputProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@Configuration
@EnableConfigurationProperties(ApplicationOutputProperties::class)
class PowerMonitoringApplication

fun main(args: Array<String>) {
    runApplication<PowerMonitoringApplication>(*args)
}
