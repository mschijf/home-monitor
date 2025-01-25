package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerTest(
) {

    @Tag(name="Test")
    @GetMapping("/test/play_new_stuff")
    fun someTest(): Any {
        return "Can be used for test purposes. Currently doing nothing"
    }

}