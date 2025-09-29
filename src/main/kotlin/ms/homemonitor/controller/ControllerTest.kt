package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.smartplug.restclient.TuyaClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerTest(
    val tuyaClient: TuyaClient) {

    @Tag(name="Test")
    @GetMapping("/test/do_test")
    fun someTest(): Any {
//        println("DO REFRESH")
//        tuyaAccessToken.refreshTuyaAccessCode("a2ede1da3fa9e1bdfc1e38650c430c31")
//        println("END REFRESH")
        return tuyaClient.getTuyaDataAllDevices()
    }

}