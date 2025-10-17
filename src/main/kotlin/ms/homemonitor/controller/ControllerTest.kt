package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.smartplug.service.SmartPlugService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerTest(
    val smartPlugService: SmartPlugService) {

    @Tag(name="Test")
    @GetMapping("/test/do_test")
    fun someTest(): Any {
        return smartPlugService.processMeasurement()
    }

}