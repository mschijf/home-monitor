package ms.homemonitor.dbstats.domain.model

import java.time.LocalDateTime

data class BackupStats(val fileSize: Long, val dateTime: LocalDateTime)