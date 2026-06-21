package ms.homemonitor.weather.repository

import ms.homemonitor.weather.repository.model.WeatherConditionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WeatherConditionRepository : JpaRepository<WeatherConditionEntity, Long> {
    fun findByText(text: String): WeatherConditionEntity?
}
