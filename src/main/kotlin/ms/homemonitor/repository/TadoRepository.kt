package ms.homemonitor.repository

import ms.homemonitor.config.ApplicationOutputProperties
import ms.homemonitor.infra.tado.model.TadoResponseModel
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TadoRepository(
    applicationOutputProperties: ApplicationOutputProperties
) : CsvRepository(applicationOutputProperties) {

    private val baseFileName = "tadoTemperature"

    fun storeTadoData(data: TadoResponseModel) {
        store(baseFileName, data.toCSV(), csvHeader)
    }

    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val csvHeader = "time;" +
            "insideTemperature;humidity;heatingPower;" +
            "settingPower;settingTemperature;" +
            "outsideTemperature;solarIntensity;weatherState\n"

    fun TadoResponseModel.toCSV(): String {
        return "${LocalDateTime.now().format(timeFormat)};" +
                String.format("%.3f", this.tadoState.sensorDataPoints.insideTemperature.celsius) + ";" +
                String.format("%.3f", this.tadoState.sensorDataPoints.humidity.percentage) + ";" +
                String.format("%.3f", this.tadoState.activityDataPoints.heatingPower.percentage) + ";" +
                this.tadoState.setting.power + ";" +
                String.format("%.3f", this.tadoState.setting.temperature) + ";" +
                String.format("%.3f", this.weather.outsideTemperature.celsius) + ";" +
                String.format("%.3f", this.weather.solarIntensity.percentage) + ";" +
                "${this.weather.weatherState.value}\n"
    }
}