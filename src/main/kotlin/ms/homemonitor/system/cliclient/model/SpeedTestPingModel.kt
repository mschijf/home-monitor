package ms.homemonitor.system.cliclient.model

data class SpeedTestPingModel(
    val jitter: Double,
    val latency: Double,
    val low: Double,
    val high: Double,
)
