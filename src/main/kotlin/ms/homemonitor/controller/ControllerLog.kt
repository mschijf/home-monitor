package ms.homemonitor.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.transaction.Transactional
import ms.homemonitor.dbstats.cliclient.model.BackupStats
import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.eneco.domain.service.EnecoService
import ms.homemonitor.power.domain.service.HomeWizardPowerService
import ms.homemonitor.power.restclient.model.HomeWizardEnergyData
import ms.homemonitor.water.restclient.model.HomeWizardWaterData
import ms.homemonitor.power.restclient.HomeWizardEnergyClient
import ms.homemonitor.log.domain.service.LogService
import ms.homemonitor.log.domain.model.LogLine
import ms.homemonitor.raspberrypi.domain.model.RaspberryPiStatsModel
import ms.homemonitor.raspberrypi.cliclient.RaspberryPiStats
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.tado.restclient.model.TadoResponseModel
import ms.homemonitor.tado.restclient.TadoClient
import ms.homemonitor.water.domain.service.HomeWizardWaterService
import ms.homemonitor.water.restclient.HomeWizardWaterClient
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ControllerLog(
    private val logService: LogService,
) {

    private val log = LoggerFactory.getLogger(ControllerLog::class.java)

    @Tag(name="Log")
    @GetMapping("/log/logs")
    fun getLogs(): List<LogLine> {
        return logService.getLogs()
    }
}