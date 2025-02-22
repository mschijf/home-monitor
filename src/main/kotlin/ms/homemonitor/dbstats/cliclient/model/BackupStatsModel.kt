package ms.homemonitor.dbstats.cliclient.model

import java.time.LocalDateTime

data class BackupStatsModel(val fileSize: Long, val dateTime: LocalDateTime)