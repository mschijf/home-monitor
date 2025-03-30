package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.heath.service.HeathService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerTest(private val heathService: HeathService) {

    @Tag(name="Test")
    @GetMapping("/test/do eneco_update")
    fun someTest(): Any {
        val result = heathService.processMeaurement()
        return "Updated ok: $result"
    }

}