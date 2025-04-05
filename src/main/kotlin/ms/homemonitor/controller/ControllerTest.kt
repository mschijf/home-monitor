package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.tado.service.TadoService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerTest(private val tadoService: TadoService) {

    @Tag(name="Test")
    @GetMapping("/test/do tado_history_test")
    fun someTest(): Any {
        val result = tadoService.processHistory()
        return "Updated ok: $result"
    }

}