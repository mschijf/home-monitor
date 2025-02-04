package ms.homemonitor.dbstats.service

import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.dbstats.repository.BackupStatsRepository
import ms.homemonitor.dbstats.repository.BackupStatsEntity
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val dbStatsRepository: BackupStatsRepository,
    private val measurement: MicroMeterMeasurement,
    private val dbStats: DbStats,
) {

    fun processDbStats() {
        val dbSize = dbStatsRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    fun processBackupStats() {
        val stats = dbStats.getBackupStats()
        val freeSpace = dbStats.getFreeBackupSpace()
        if (stats.isNotEmpty()) {
            val entity = dbStatsRepository.findById(1).orElse(BackupStatsEntity(1))

            entity.oldest = stats.first().dateTime
            entity.last = stats.last().dateTime
            entity.size = stats.last().fileSize
            entity.freeSpace = freeSpace
            dbStatsRepository.saveAndFlush(entity)

            measurement.setDoubleGauge("homeMonitorBackupSize", stats.last().fileSize.toDouble())
        }
    }
}