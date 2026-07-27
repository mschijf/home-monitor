package ms.homemonitor.system.cliclient.model

data class SpeedTestTransferModel(
    val bandwidth: Long,
    val bytes: Long,
    val elapsed: Long,
    val latency: SpeedTestLatencyModel,
)
