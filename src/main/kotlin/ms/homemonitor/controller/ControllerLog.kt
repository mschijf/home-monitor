package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.log.domain.service.LogService
import ms.homemonitor.log.domain.model.LogLine
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerLog(
    private val logService: LogService,
) {

    private val log = LoggerFactory.getLogger(ControllerLog::class.java)

    @Tag(name="Log")
    @GetMapping("/log/logs")
    fun getLogs(): List<LogLine> {
        return logService.getLogs()
    }
}