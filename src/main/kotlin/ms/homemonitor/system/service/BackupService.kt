package ms.homemonitor.system.service

import ms.homemonitor.shared.tools.micrometer.MicroMeterMeasurement
import ms.homemonitor.system.cliclient.SystemClient
import ms.homemonitor.system.cliclient.model.BackupResultModel
import org.springframework.stereotype.Service

@Service
class BackupService(
    private val systemClient: SystemClient,
    private val measurement: MicroMeterMeasurement,
) {

    fun executeBackup(keep: Int) {
        val result = systemClient.executeBackup(keep)
        setMetrics(result)
    }

    private fun setMetrics(data: BackupResultModel) {
        measurement.setDoubleGauge("systemBackupSizeKb", data.size.toDouble())
    }
}