package ms.homemonitor.shared.admin.data.model

enum class AdminKey(val type: AdminType) {
    LAST_ENECO_UPDATE(AdminType.TIMESTAMP),
    LAST_BACKUP_TIME(AdminType.TIMESTAMP),
    LAST_BACKUP_SIZE(AdminType.LONG),
    OLDEST_BACKUP_TIME(AdminType.TIMESTAMP),
    FREE_SPACE_FOR_BACKUP(AdminType.LONG),
    LAST_STARTUP_TIME(AdminType.TIMESTAMP)
}

enum class AdminType {
    TIMESTAMP, LONG
}