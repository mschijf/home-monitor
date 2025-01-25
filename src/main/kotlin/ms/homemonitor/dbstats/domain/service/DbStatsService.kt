package ms.homemonitor.dbstats.domain.service

import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.dbstats.data.repository.DBStatsRepository
import ms.homemonitor.shared.admin.data.model.AdminKey
import ms.homemonitor.shared.admin.data.repository.AdminRepositoryTool
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val dbStatsRepository: DBStatsRepository,
    private val adminRepositoryTool: AdminRepositoryTool,
    private val measurement: MicroMeterMeasurement,
    private val dbStats: DbStats,
) {

    fun processDbStats() {
        measureDbStats()
        measureBackupStats()
    }

    fun processDropboxFreeSpace() {
        adminRepositoryTool.updateAdminRecord(AdminKey.FREE_SPACE_FOR_BACKUP, dbStats.getFreeBackupSpace())
    }

    private fun measureDbStats() {
        val dbSize = dbStatsRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    private fun measureBackupStats() {
        val stats = dbStats.getBackupStats()
        if (stats.isNotEmpty()) {
            adminRepositoryTool.updateAdminRecord(AdminKey.LAST_BACKUP_TIME, stats.last().dateTime)
            adminRepositoryTool.updateAdminRecord(AdminKey.OLDEST_BACKUP_TIME, stats.first().dateTime)
            adminRepositoryTool.updateAdminRecord(AdminKey.LAST_BACKUP_SIZE, stats.last().fileSize)
            measurement.setDoubleGauge("homeMonitorBackupSize", stats.last().fileSize.toDouble())
        }
    }
}