package ms.homemonitor.dbstats.service

import ms.homemonitor.dbstats.cliclient.BackupStats
import ms.homemonitor.dbstats.repository.BackupStatsRepository
import ms.homemonitor.dbstats.repository.BackupStatsEntity
import ms.homemonitor.dbstats.repository.DatabaseAdminRepository
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val backupStatsRepository: BackupStatsRepository,
    private val databaseAdminRepository: DatabaseAdminRepository,
    private val measurement: MicroMeterMeasurement,
    private val backupStats: BackupStats,
) {

    fun processDbStats() {
        val dbSize = databaseAdminRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    fun processBackupStats() {
        val stats = backupStats.getBackupStats()
        val freeSpace = backupStats.getFreeBackupSpace()
        if (stats.isNotEmpty()) {
            val entity = backupStatsRepository.findById(1).orElse(BackupStatsEntity(1))

            entity.oldest = stats.first().dateTime
            entity.last = stats.last().dateTime
            entity.size = stats.last().fileSize
            entity.freeSpace = freeSpace
            backupStatsRepository.saveAndFlush(entity)

            measurement.setDoubleGauge("homeMonitorBackupSize", stats.last().fileSize.toDouble())
        }
    }
}