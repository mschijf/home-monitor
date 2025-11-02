package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.system.service.SystemService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerTest(
    val systemService: SystemService) {

    @Tag(name="Test")
    @GetMapping("/test/do_test")
    fun someTest(): Any {
        return systemService.cleanUp()
    }

}