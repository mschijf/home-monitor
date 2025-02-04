package ms.homemonitor.shared.admin.repository

import ms.homemonitor.shared.admin.repository.model.AdminKey
import ms.homemonitor.shared.admin.repository.model.AdminTimestampEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AdminRepositoryTool(
    private val adminTimestampRepository: AdminTimestampRepository
) {

    fun updateAdminTimestampRecord(key: AdminKey, value: Any) {
        val timeStampRecord = getLastTimestampEntity(key)
        timeStampRecord.time = value as LocalDateTime
        adminTimestampRepository.saveAndFlush(timeStampRecord)
    }

    fun getAdminTimestamp(key: AdminKey): LocalDateTime? {
        val timeStampRecord = getLastTimestampEntity(key)
        return timeStampRecord.time
    }

    private fun getLastTimestampEntity(key: AdminKey): AdminTimestampEntity {
        return adminTimestampRepository
            .findById(key.toString())
            .orElse(AdminTimestampEntity(key = key.toString(), time = null))
    }

}