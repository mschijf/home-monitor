package ms.homemonitor.domain.dbstats.model

import java.time.LocalDateTime

data class BackupStats(val fileSize: Long, val dateTime: LocalDateTime)
