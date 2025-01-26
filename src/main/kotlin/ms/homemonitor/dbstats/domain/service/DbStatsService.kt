package ms.homemonitor.dbstats.domain.service

import ms.homemonitor.dbstats.cliclient.DbStats
import ms.homemonitor.dbstats.data.repository.BackupStatsRepository
import ms.homemonitor.dbstats.data.repository.BackupStatsEntity
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val dbStatsRepository: BackupStatsRepository,
    private val measurement: MicroMeterMeasurement,
    private val dbStats: DbStats,
) {

    fun processDbStats() {
        measureDbStats()
    }

    fun processBackupStats() {
        measureBackupStats()
    }

    private fun measureDbStats() {
        val dbSize = dbStatsRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    private fun measureBackupStats() {
        val stats = dbStats.getBackupStats()
        val freeSpace = dbStats.getFreeBackupSpace()
        if (stats.isNotEmpty()) {
            val entity = dbStatsRepository.findById(1).orElse(BackupStatsEntity(1))

            entity.oldest = stats.first().dateTime
            entity.last = stats.last().dateTime
            entity.size = stats.last().fileSize
            entity.freeSpace = freeSpace

            measurement.setDoubleGauge("homeMonitorBackupSize", stats.last().fileSize.toDouble())
        }
    }
}