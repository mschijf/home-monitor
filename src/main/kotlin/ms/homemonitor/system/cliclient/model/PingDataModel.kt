package ms.homemonitor.system.cliclient.model

import java.time.LocalDateTime

data class PingDataModel(
    val runDateTime: LocalDateTime,
    val packetsTransmitted: Int,
    val packetsReceived: Int,
    val minTimeMillis: Double,
    val avgTimeMillis: Double,
    val maxTimeMillis: Double,
    val mDev: Double,
    val host: String,
)
