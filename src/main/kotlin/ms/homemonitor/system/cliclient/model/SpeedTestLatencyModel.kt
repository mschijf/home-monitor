package ms.homemonitor.system.cliclient.model

data class SpeedTestLatencyModel(
    val iqm: Double,
    val low: Double,
    val high: Double,
    val jitter: Double,
)
