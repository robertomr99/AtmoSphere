package com.robertomr99.atmosphere.domain.weather.entities

import com.robertomr99.atmosphere.domain.weather.entities.forecast.City
import com.robertomr99.atmosphere.domain.weather.entities.forecast.CustomList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResult (
    @SerialName("cod") val cod: String? = null,
    @SerialName("message") val message: Int? = null,
    @SerialName("cnt") val cnt: Int? = null,
    @SerialName("list") val list: List<CustomList>? = null,
    @SerialName("city") val city: City? = null
)

fun ForecastResult.toEntityList(cityOwnerId: String): List<ForecastEntity> {
    if (list.isNullOrEmpty()) {
        return emptyList()
    }

    return list.mapNotNull { forecast ->
        val hour = forecast.dt ?: return@mapNotNull null
        val main = forecast.main ?: return@mapNotNull null
        val icon = forecast.weather?.firstOrNull()?.icon ?: "01d"

        if (cityOwnerId.isBlank()) {
            return@mapNotNull null
        }

        ForecastEntity(
            cityOwnerId = cityOwnerId,
            hour = hour,
            temp = main.temp?.toInt() ?: 0,
            tempMin = main.tempMin?.toInt() ?: 0,
            tempMax = main.tempMax?.toInt() ?: 0,
            weatherIcon = icon
        )
    }
}