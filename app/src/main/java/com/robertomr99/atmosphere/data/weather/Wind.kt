package com.robertomr99.atmosphere.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wind(
    @SerialName("deg") val deg: Int? = null,
    @SerialName("gust") val gust: Double? = null,
    @SerialName("speed") val speed: Double? = null
)