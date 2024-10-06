package ms.homemonitor

import ms.homemonitor.domain.eneco.EnecoProperties
import ms.homemonitor.domain.homewizard.HomeWizardProperties
import ms.homemonitor.domain.raspberrypi.RaspberryPiProperties
import ms.homemonitor.domain.tado.TadoProperties
import ms.homemonitor.domain.weerlive.WeerLiveProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@Configuration
@EnableConfigurationProperties(
    DataOutputProperties::class,
    HomeWizardProperties::class,
    TadoProperties::class,
    RaspberryPiProperties::class,
    WeerLiveProperties::class,
    EnecoProperties::class)

class HomeMonitorApplication

fun main(args: Array<String>) {
    runApplication<HomeMonitorApplication>(*args)
}
