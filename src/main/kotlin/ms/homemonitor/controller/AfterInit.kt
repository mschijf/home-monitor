package ms.homemonitor.controller

import ms.homemonitor.shared.admin.repository.model.AdminKey
import ms.homemonitor.shared.admin.repository.AdminRepositoryTool
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AfterInit(
    private val adminRepositoryTool: AdminRepositoryTool
): ApplicationListener<ContextRefreshedEvent>  {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        adminRepositoryTool
            .updateAdminTimestampRecord(AdminKey.LAST_STARTUP_TIME, LocalDateTime.now())
    }

}