package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.system.service.SystemService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerTest(val systemService: SystemService) {

    @Tag(name="Test")
    @GetMapping("/test/play_new_stuff")
    fun someTest(): Any {
        systemService.executeBackup(670)
        return "Doing nothing"
    }

}