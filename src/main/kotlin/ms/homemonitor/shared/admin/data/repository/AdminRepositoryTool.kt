package ms.homemonitor.shared.admin.data.repository

import ms.homemonitor.shared.admin.data.model.AdminEntity
import ms.homemonitor.shared.admin.data.model.AdminKey
import ms.homemonitor.shared.admin.data.model.AdminTimestampEntity
import ms.homemonitor.shared.admin.data.model.AdminType
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AdminRepositoryTool(
    private val adminRepository: AdminRepository,
    private val adminTimestampRepository: AdminTimestampRepository
) {

    fun updateAdminRecord(key: AdminKey, value: Any) {
        val lastUpdate = getLastUpdate(key)
        lastUpdate.value = value.toString()
        adminRepository.saveAndFlush(lastUpdate)
        if (key.type == AdminType.TIMESTAMP) {
            val ts = getLastTimestamp(key)
            ts.time = value as LocalDateTime
            adminTimestampRepository.saveAndFlush(ts)
        }
    }

    fun getAdminValue(key: AdminKey): Any? {
        val value = getLastUpdate(key).value
        return when (key.type) {
            AdminType.LONG -> value?.toLong()
            AdminType.TIMESTAMP -> if (value != null) LocalDateTime.parse(value) else null
        }
    }

    private fun getLastUpdate(key: AdminKey): AdminEntity {
        return adminRepository
            .findById(key.toString())
            .orElse(AdminEntity(key = key.toString(), type = key.type.toString(), value = null))
    }

    private fun getLastTimestamp(key: AdminKey): AdminTimestampEntity {
        return adminTimestampRepository
            .findById(key.toString())
            .orElse(AdminTimestampEntity(key = key.toString(), time = null))
    }

}