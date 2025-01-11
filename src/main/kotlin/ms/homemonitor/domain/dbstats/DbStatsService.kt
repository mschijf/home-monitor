package ms.homemonitor.domain.dbstats

import ms.homemonitor.domain.dbstats.rest.DbStats
import ms.homemonitor.micrometer.MicroMeterMeasurement
import ms.homemonitor.repository.admin.AdminEntity
import ms.homemonitor.repository.admin.AdminKey
import ms.homemonitor.repository.admin.AdminRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val adminRepository: AdminRepository,
    private val measurement: MicroMeterMeasurement,
    private val dbStats: DbStats,
    @Value("\${dbstats.enabled}") private val enabled: Boolean,
) {

    @Scheduled(cron = "0 * * * * *")
    fun dbStats() {
        if (!enabled)
            return
        measureDbStats()
        measureBackupStats()
    }

    private fun measureDbStats() {
        val dbSize = adminRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    private fun measureBackupStats() {
        val stats = dbStats.getBackupStats()
        if (stats.isNotEmpty()) {
            updateAdminRecord(AdminKey.LAST_BACKUP_TIME.toString(), stats.last().dateTime.toString())
            updateAdminRecord(AdminKey.LAST_BACKUP_SIZE.toString(), stats.last().fileSize.toString())
            updateAdminRecord(AdminKey.OLDEST_BACKUP_TIME.toString(), stats.first().dateTime.toString())
            measurement.setDoubleGauge("homeMonitorBackupSize", stats.last().fileSize.toDouble())
        }
    }

    private fun updateAdminRecord(key: String, value: String) {
        val lastUpdate = adminRepository
            .findById(key)
            .orElse(AdminEntity(key=key, value=value))

        lastUpdate.value = value
        adminRepository.saveAndFlush(lastUpdate)
    }
}