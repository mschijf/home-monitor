package ms.homemonitor.dbstats.domain.service

import ms.homemonitor.dbstats.commandline.DbStats
import ms.homemonitor.shared.admin.data.model.AdminEntity
import ms.homemonitor.shared.admin.data.model.AdminKey
import ms.homemonitor.dbstats.data.repository.DBStatsRepository
import ms.homemonitor.shared.admin.data.repository.AdminRepository
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val dbStatsRepository: DBStatsRepository,
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

    @Scheduled(cron = "0 0 * * * *")
    fun dropboxFreeSpace() {
        if (!enabled)
            return
        updateAdminRecord(AdminKey.FREE_SPACE_FOR_BACKUP.toString(), dbStats.getFreeBackupSpace().toString())
    }


    private fun measureDbStats() {
        val dbSize = dbStatsRepository.getDatabaseSize("home-monitor")
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
            .orElse(AdminEntity(key = key, value = value))

        lastUpdate.value = value
        adminRepository.saveAndFlush(lastUpdate)
    }
}