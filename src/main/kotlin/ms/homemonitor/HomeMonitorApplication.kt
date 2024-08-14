package ms.homemonitor

import ms.homemonitor.config.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@Configuration
@EnableConfigurationProperties(
    ApplicationOutputProperties::class,
    HomeWizardProperties::class,
    TadoProperties::class,
    RaspberryPiProperties::class,
    WeerLiveProperties::class,
    EnecoProperties::class)

class HomeMonitorApplication

fun main(args: Array<String>) {
    runApplication<HomeMonitorApplication>(*args)
}
