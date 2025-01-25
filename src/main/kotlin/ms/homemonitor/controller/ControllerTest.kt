package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.tado.domain.service.TadoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerTest(
    private val tadoService: TadoService
) {

    @Tag(name="Test")
    @GetMapping("/test/play_new_stuff")
    fun someTest(): Any {
        tadoService.processDayReports2()
        return "Can be used for test purposes. Currently doing nothing"
    }

}