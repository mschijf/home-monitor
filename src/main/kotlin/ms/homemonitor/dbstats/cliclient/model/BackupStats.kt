package ms.homemonitor.dbstats.cliclient.model

import java.time.LocalDateTime

data class BackupStats(val fileSize: Long, val dateTime: LocalDateTime)