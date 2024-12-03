package ms.homemonitor.domain.dbstats

import ms.homemonitor.domain.dbstats.rest.DbStats
import ms.homemonitor.repository.AdminEntity
import ms.homemonitor.repository.AdminKey
import ms.homemonitor.repository.AdminRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class DbStatsService(
    private val adminRepository: AdminRepository,
    private val dbStats: DbStats,
    @Value("\${dbstats.enabled}") private val enabled: Boolean) {

    @Scheduled(cron = "0 * * * * *")
    fun backUpStats() {
        if (!enabled)
            return
        val stats = dbStats.getBackupStatsOrNull()
        if (stats != null) {
            updateAdminRecord(AdminKey.LAST_BACKUP_TIME.toString(), stats.dateTime.toString())
            updateAdminRecord(AdminKey.LAST_BACKUP_SIZE.toString(), stats.fileSize.toString())
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