package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.tuya.restclient.TuyaAccessToken
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@RestController
class ControllerTest(
    val tuyaAccessToken: TuyaAccessToken) {

    @Tag(name="Test")
    @GetMapping("/test/do_test")
    fun someTest(): Any {
//        println("DO REFRESH")
//        tuyaAccessToken.refreshTuyaAccessCode("a2ede1da3fa9e1bdfc1e38650c430c31")
//        println("END REFRESH")
        return tuyaAccessToken.getTuyaDataAllDevices()
    }

}