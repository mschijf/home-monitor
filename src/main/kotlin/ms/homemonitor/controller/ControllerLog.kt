package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.log.service.LogService
import ms.homemonitor.log.service.model.LogLine
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerLog(
    private val logService: LogService,
) {

    @Tag(name="Log")
    @GetMapping("/log/logs")
    fun getLogs(): List<LogLine> {
        return logService.getLogs()
    }
}