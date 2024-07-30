package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.tado.model.TadoResponseModel
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TadoRepository(
    applicationOutputProperties: ApplicationOutputProperties): CsvRepository(applicationOutputProperties) {

    private val baseFileName = "tadoTemperature"

    fun storeTadoData(data: TadoResponseModel) {
        store(baseFileName, data.toCSV())
    }

    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun TadoResponseModel.toCSV(): String {
        return "${LocalDateTime.now().format(timeFormat)};" +
                "${this.tadoState.sensorDataPoints.insideTemperature.celsius};" +
                "${this.tadoState.sensorDataPoints.humidity.percentage};" +
                "${this.tadoState.activityDataPoints.heatingPower.percentage};" +
                "${this.tadoState.setting.power};" +
                "${this.tadoState.setting.temperature};" +
                "${this.weather.outsideTemperature.celsius};" +
                "${this.weather.solarIntensity.percentage};" +
                "${this.weather.weatherState.value};\n"
    }
}