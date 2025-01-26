package ms.homemonitor.shared.admin.data.repository

import ms.homemonitor.shared.admin.data.model.AdminTimestampEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdminTimestampRepository: JpaRepository<AdminTimestampEntity, String>