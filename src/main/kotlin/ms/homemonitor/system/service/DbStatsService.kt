package ms.homemonitor.system.service

import ms.homemonitor.system.cliclient.DropboxClient
import ms.homemonitor.system.repository.BackupStatsRepository
import ms.homemonitor.system.repository.BackupStatsEntity
import ms.homemonitor.system.repository.DatabaseAdminRepository
import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val backupStatsRepository: BackupStatsRepository,
    private val databaseAdminRepository: DatabaseAdminRepository,
    private val measurement: MicroMeterMeasurement,
    private val dropboxClient: DropboxClient,
) {

    fun processDbStats() {
        val dbSize = databaseAdminRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    fun processBackupStats() {
        val stats = dropboxClient.getBackupStats()
        val freeSpace = dropboxClient.getFreeBackupSpace()
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