package ms.homemonitor.shelly.restclient.model

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class ShellyThermometerData(
    @JsonProperty("time")
    val time: LocalDateTime = LocalDateTime.now(),

    @JsonProperty(value = "isOk")
    @JsonAlias("isOk")
    val isOk : Boolean,

    @JsonProperty(value = "data")
    @JsonAlias("data")
    val data: ShellyData,
)

data class ShellyData(
    @JsonProperty(value = "device_status")
    @JsonAlias("deviceStatus")
    val deviceStatus: ShellyDeviceStatus
)

data class ShellyDeviceStatus(
    @JsonProperty(value = "temperature:0")
    @JsonAlias("temperature")
    val temperature: ShellyTemperature,

    @JsonProperty(value = "humidity:0")
    @JsonAlias("humidity")
    val humidity: ShellyHumidity,

    @JsonProperty(value = "_updated")
    @JsonAlias("updated")
    val updated: String
)

data class ShellyTemperature(
    @JsonProperty(value = "tC")
    @JsonAlias("value")
    val value: Double
)

data class ShellyHumidity(
    @JsonProperty(value = "rh")
    @JsonAlias("value")
    val value: Double
)

