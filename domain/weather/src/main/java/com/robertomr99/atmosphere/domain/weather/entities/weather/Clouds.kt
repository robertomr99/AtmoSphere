package com.robertomr99.atmosphere.domain.weather.entities.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clouds(
    @SerialName("all") val all: Int? = null
)