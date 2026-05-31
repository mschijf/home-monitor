package ms.homemonitor.weather.restclient.model

data class WeatherApiCurrentData(
    val location: LocationData,
    val current: CurrentData
)

data class LocationData(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

data class CurrentData(
    val last_updated_epoch: Long,
    val last_updated: String?,
    val temp_c: Double,
    val temp_f: Double,
    val is_day: Boolean,
    val condition: ConditionData,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_degree: Double,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val precip_mm: Double,
    val precip_in: Double,
    val humidity: Double,
    val cloud: Double,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val windchill_c: Double,
    val windchill_f: Double,
    val heatindex_c: Double,
    val heatindex_f: Double,
    val dewpoint_c: Double,
    val dewpoint_f: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val uv: Double,
    val gust_mph: Double,
    val gust_kph: Double,
    val short_rad: Double,
    val diff_rad: Double,
    val dni: Double,
    val gti: Double
)

data class ConditionData(
    val text: String,
    val icon: String,
    val code: Long
)


