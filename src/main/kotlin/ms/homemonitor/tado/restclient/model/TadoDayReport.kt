package ms.homemonitor.tado.restclient.model

import java.time.LocalDateTime


data class TadoDayReport (
    val measuredData: MeasuredData,
    val settings: Settings,
    val callForHeat: CallForHeat,
    val weather: Weather
)


data class Weather (
    val condition: Condition,
    val sunny: Sunny
)


data class Sunny(
    val timeSeriesType: String,
    val valueType: String,
    val dataIntervals: List<SunnyDataPoint>
)


data class SunnyDataPoint (
    val from: LocalDateTime,
    val to: LocalDateTime,
    val value: Boolean
)


data class Condition(
    val timeSeriesType: String,
    val valueType: String,
    val dataIntervals: List<ConditionDataPoint>
)


data class ConditionDataPoint(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val value: ConditionDataPointValue
)


data class ConditionDataPointValue(
    val state: String,
    val temperature: TadoTemperature
)


data class CallForHeat (
    val timeSeriesType: String,
    val valueType: String,
    val dataIntervals: List<CallForHeatDataPoint>
)


data class CallForHeatDataPoint (
    val from: LocalDateTime,
    val to: LocalDateTime,
    val value: String
)


data class Settings(
    val timeSeriesType: String,
    val valueType: String,
    val dataIntervals: List<SettingDataPoint>
)


data class SettingDataPoint (
    val from: LocalDateTime,
    val to: LocalDateTime,
    val value: SettingValue
)


data class SettingValue(
    val type: String,
    val power: String,
    val temperature: TadoTemperature?
)


data class MeasuredData(
    val insideTemperature: InsideTemperature,
    val humidity: Humidity
)


data class Humidity(
    val timeSeriesType: String,
    val valueType: String,
    val min: Double,
    val max: Double,
    val dataPoints: List<HumidityDataPoint>
)


data class HumidityDataPoint(
    val timestamp: LocalDateTime,
    val value: Double
)


data class InsideTemperature(
    val timeSeriesType: String,
    val valueType: String,
    val min: TadoTemperature?,
    val max: TadoTemperature?,
    val dataPoints: List<TemperatureDataPoint>
)


data class TemperatureDataPoint(
    val timestamp: LocalDateTime,
    val value: TadoTemperature
)

