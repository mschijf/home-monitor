package ms.homemonitor.weather.repository

import ms.homemonitor.weather.repository.model.WeatherEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface WeatherRepository: JpaRepository<WeatherEntity, LocalDateTime> {

}