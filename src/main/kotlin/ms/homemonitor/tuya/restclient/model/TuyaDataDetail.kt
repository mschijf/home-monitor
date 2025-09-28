package ms.homemonitor.tuya.restclient.model

data class TuyaDataDetail(
    val code: String,
    val eventTime: Long,
    val value: Int
)
