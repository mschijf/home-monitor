package ms.homemonitor.system.service

import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.system.cliclient.BackupClient
import ms.homemonitor.system.cliclient.DropboxClient
import ms.homemonitor.system.cliclient.SystemTemperatureClient
import ms.homemonitor.system.repository.BackupStatsEntity
import ms.homemonitor.system.repository.BackupStatsRepository
import ms.homemonitor.system.repository.DatabaseAdminRepository
import org.springframework.stereotype.Service
import kotlin.math.max

@Service
class SystemService(
    private val systemTemperatureClient: SystemTemperatureClient,
    private val backupClient: BackupClient,
    private val backupStatsRepository: BackupStatsRepository,
    private val databaseAdminRepository: DatabaseAdminRepository,
    private val dropboxClient: DropboxClient,
    private val measurement: MicroMeterMeasurement
) {

    fun processMeasurement() {
        val data = systemTemperatureClient.getSystemTemperature()
        measurement.setDoubleGauge("systemCpuTemperature", data.cpuTemperature)
        measurement.setDoubleGauge("systemGpuTemperature", data.gpuTemperature)
    }

    fun processDbStats() {
        val dbSize = databaseAdminRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    fun executeBackup(keep: Int) {
        val data = backupClient.executeBackup("_postgres", keep)
        measurement.setDoubleGauge("homeMonitorBackupSize", data.fileSize.toDouble())
        cleanUp(keep)
    }

    private fun cleanUp(keep: Int) {
        val backupList = dropboxClient.getBackupStats(filter = "_postgres")
        if (backupList.size > keep) {
            backupList.take(backupList.size - keep).forEach { dropboxRecord ->
                dropboxClient.deleteFile(dropboxRecord.fileName)
            }
        }
        measurement.setDoubleGauge("homeMonitorBackupCount", max(keep, backupList.size).toDouble())
    }

    fun processBackupStats() {
        val stats = dropboxClient.getBackupStats(filter = "_postgres")
        val freeSpace = dropboxClient.getFreeBackupSpace()
        if (stats.isNotEmpty()) {
            val entity = backupStatsRepository.findById(1).orElse(BackupStatsEntity(1))

            entity.oldest = stats.first().dateTime
            entity.last = stats.last().dateTime
            entity.size = stats.last().fileSize
            entity.freeSpace = freeSpace
            backupStatsRepository.saveAndFlush(entity)
        }
    }

}