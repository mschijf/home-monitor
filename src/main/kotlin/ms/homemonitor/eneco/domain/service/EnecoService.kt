package ms.homemonitor.eneco.domain.service

import jakarta.transaction.Transactional
import ms.homemonitor.eneco.restclient.EnecoRestClient
import ms.homemonitor.shared.summary.domain.service.SummaryService
import ms.homemonitor.shared.summary.domain.model.YearSummary
import ms.homemonitor.eneco.data.model.HeathEntity
import ms.homemonitor.shared.admin.data.model.AdminEntity
import ms.homemonitor.shared.admin.data.model.AdminKey
import ms.homemonitor.eneco.data.repository.HeathRepository
import ms.homemonitor.shared.admin.data.repository.AdminRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class EnecoService(
    private val heathRepository: HeathRepository,
    private val summary: SummaryService,
) {
    fun getYearSummary(): YearSummary {
        return summary.getSummary(heathRepository)
    }
}