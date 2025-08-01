package com.robertomr99.atmosphere.domain.weather.entities.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Main(
    @SerialName("temp") val temp: Double? = null,
    @SerialName("feels_like") val feelsLike: Double? = null,
    @SerialName("temp_min") val tempMin: Double? = null,
    @SerialName("temp_max") val tempMax: Double? = null,
    @SerialName("pressure") val pressure: Int? = null,
    @SerialName("humidity") val humidity: Int? = null,
    @SerialName("sea_level") val seaLevel: Int? = null,
    @SerialName("grnd_level") val grndLevel: Int? = null
)