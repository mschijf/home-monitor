package ms.homemonitoring

import ms.homemonitoring.config.ApplicationOutputProperties
import ms.homemonitoring.config.HomeWizardProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@Configuration
@EnableConfigurationProperties(ApplicationOutputProperties::class, HomeWizardProperties::class)
class HomeMonitoringApplication

fun main(args: Array<String>) {
    runApplication<HomeMonitoringApplication>(*args)
}
