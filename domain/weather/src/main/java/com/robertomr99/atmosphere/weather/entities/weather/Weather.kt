package com.robertomr99.atmosphere.weather.entities.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    @SerialName("id") val id: Int? = null,
    @SerialName("main") val main: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("icon") val icon: String? = null
)