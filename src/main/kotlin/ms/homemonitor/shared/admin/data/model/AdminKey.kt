package ms.homemonitor.shared.admin.data.model

enum class AdminKey(val type: AdminType) {
    LAST_ENECO_UPDATE(AdminType.TIMESTAMP),
    LAST_STARTUP_TIME(AdminType.TIMESTAMP)
}

enum class AdminType {
    TIMESTAMP, LONG
}