package ms.homewizardapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MessageController(
    private val homeWizardDataClient: HomeWizardDataClient
) {

    @GetMapping("/")
    fun index(): String {
        return homeWizardDataClient.getHomeWizardData().toString()
    }

}