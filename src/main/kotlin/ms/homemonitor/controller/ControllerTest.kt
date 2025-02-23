package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import ms.homemonitor.system.service.BackupService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerTest(
    val backupService: BackupService
) {

    @Tag(name="Test")
    @GetMapping("/test/play_new_stuff")
    fun someTest(): Any {
        backupService.executeBackup()
        return "Backup Done"
    }

}