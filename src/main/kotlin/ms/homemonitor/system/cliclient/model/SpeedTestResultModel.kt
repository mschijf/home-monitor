package ms.homemonitor.system.cliclient.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class SpeedTestResultModel(
    val type: String,
    val timestamp: Instant,
    val ping: SpeedTestPingModel,
    val download: SpeedTestTransferModel,
    val upload: SpeedTestTransferModel,
    val packetLoss: Double,
    val isp: String,
    @JsonProperty("interface") val networkInterface: SpeedTestInterfaceModel,
    val server: SpeedTestServerModel,
    val result: SpeedTestResultDetailModel,
)
