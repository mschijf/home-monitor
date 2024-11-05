package ms.homemonitor.domain.tado.repository

import ms.homemonitor.application.DataOutputProperties
import ms.homemonitor.tools.CsvFile
import ms.homemonitor.domain.tado.model.TadoResponseModel
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class TadoRepository(dataOutputProperties: DataOutputProperties) {

    private val tadoCsvFile = CsvFile(
        path = dataOutputProperties.path,
        fileName = "tadoTemperature.csv",
        header = "time;" +
                "insideTemperature;humidity;heatingPower;" +
                "settingPower;settingTemperature;" +
                "outsideTemperature;solarIntensity;weatherState"
    )

    fun storeTadoData(data: TadoResponseModel) {
        tadoCsvFile.append(data.toCSV())
    }

    private val timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun TadoResponseModel.toCSV(): String {
        return "${LocalDateTime.now().format(timeFormat)};" +
                String.format("%.3f", this.tadoState.sensorDataPoints.insideTemperature.celsius) + ";" +
                String.format("%.3f", this.tadoState.sensorDataPoints.humidity.percentage) + ";" +
                String.format("%.3f", this.tadoState.activityDataPoints.heatingPower.percentage) + ";" +
                this.tadoState.setting.power + ";" +
                String.format("%.3f", this.tadoState.setting.temperature?.celsius?:0.0) + ";" +
                String.format("%.3f", this.weather.outsideTemperature.celsius) + ";" +
                String.format("%.3f", this.weather.solarIntensity.percentage) + ";" +
                "${this.weather.weatherState.value}"
    }
}