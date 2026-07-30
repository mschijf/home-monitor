package ms.homemonitor.system.service

import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.system.cliclient.BackupClient
import ms.homemonitor.system.cliclient.DropboxClient
import ms.homemonitor.system.cliclient.PingClient
import ms.homemonitor.system.cliclient.SpeedTestClient
import ms.homemonitor.system.cliclient.SystemTemperatureClient
import ms.homemonitor.system.repository.BackupStatsEntity
import ms.homemonitor.system.repository.BackupStatsRepository
import ms.homemonitor.system.repository.DatabaseAdminRepository
import ms.homemonitor.system.repository.SpeedTestStatsEntity
import ms.homemonitor.system.repository.SpeedTestStatsRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class SystemService(
    private val systemTemperatureClient: SystemTemperatureClient,
    private val backupClient: BackupClient,
    private val backupStatsRepository: BackupStatsRepository,
    private val databaseAdminRepository: DatabaseAdminRepository,
    private val dropboxClient: DropboxClient,
    private val measurement: MicroMeterMeasurement,
    @Value("\${home-monitor.system.backup.keepWeeks}") private val keepNumberOfWeekDetailsBackupFiles: Int,
    private val pingClient: PingClient,
    private val speedTestClient: SpeedTestClient,
    private val speedTestStatsRepository: SpeedTestStatsRepository,
) {

    fun processMeasurement() {
        val data = systemTemperatureClient.getSystemTemperature()
        measurement.setDoubleGauge("systemCpuTemperature", data.cpuTemperature)
        measurement.setDoubleGauge("systemGpuTemperature", data.gpuTemperature)
    }

    fun processPing() {
        val data = pingClient.getPingData()
        measurement.setDoubleGauge("pingPackageLost", (data.packetsTransmitted - data.packetsReceived).toDouble())
        measurement.setDoubleGauge("pingMinTime", data.minTimeMillis)
        measurement.setDoubleGauge("pingAvgTime", data.avgTimeMillis)
        measurement.setDoubleGauge("pingMaxTime", data.maxTimeMillis)
    }

    fun processSpeedTest() {
        val data = speedTestClient.getSpeedTestData()
//        measurement.setDoubleGauge("speedTestDownloadMbps", data.download.bandwidth.toDouble() / 125_000.0)
//        measurement.setDoubleGauge("speedTestUploadMbps", data.upload.bandwidth.toDouble() / 125_000.0)
//        measurement.setDoubleGauge("speedTestDownloadJitter", data.download.latency.jitter)
//        measurement.setDoubleGauge("speedTestUploadJitter", data.upload.latency.jitter)

        speedTestStatsRepository.saveAndFlush(SpeedTestStatsEntity(
            time = LocalDateTime.ofInstant(data.timestamp, ZoneId.systemDefault()),
            downloadSpeed = data.download.bandwidth.toDouble() / 125_000.0,
            uploadSpeed = data.upload.bandwidth.toDouble() / 125_000.0,
            downloadJitter = data.download.latency.jitter,
            uploadJitter = data.upload.latency.jitter,
        ))
    }

    fun processDbStats() {
        val dbSize = databaseAdminRepository.getDatabaseSize("home-monitor")
        measurement.setDoubleGauge("homeMonitorDbSize", dbSize.toDouble())
    }

    fun executeBackup() {
        val data = backupClient.executeBackup()
        measurement.setDoubleGauge("homeMonitorBackupSize", data.fileSize.toDouble())
    }

    fun cleanUp(keepWeekDetails: Int = keepNumberOfWeekDetailsBackupFiles) {
        //keep for 10 years, the latest per month
        // keep for the last x weeks, the backup details per hour
        val oldBackupsPerMonth = dropboxClient.getBackupStats()
            .filter { it.dateTime.isBefore(LocalDateTime.now().minusWeeks(keepWeekDetails.toLong())) }
            .groupBy { (it.dateTime.year % 10) * 100 + it.dateTime.month.value }
            .filter{ it.value.size > 1}

        oldBackupsPerMonth.values.forEach { monthList ->
            monthList.sortedBy { it.dateTime }.dropLast(1).forEach { dropboxRecord ->
                dropboxClient.deleteFile(dropboxRecord.fileName)
            }
        }
    }

    fun processBackupStats() {
        val stats = dropboxClient.getBackupStats()
        val freeSpace = dropboxClient.getFreeBackupSpace()
        if (stats.isNotEmpty()) {
            val entity = backupStatsRepository.findById(1).orElse(BackupStatsEntity(1))!!

            entity.oldest = stats.first().dateTime
            entity.last = stats.last().dateTime
            entity.size = stats.last().fileSize
            entity.freeSpace = freeSpace
            backupStatsRepository.saveAndFlush(entity)
        }
    }

}