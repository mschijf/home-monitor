package ms.homemonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@Configuration
class HomeMonitorApplication

fun main(args: Array<String>) {
    runApplication<HomeMonitorApplication>(*args)
}
