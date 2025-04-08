package com.robertomr99.atmosphere.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clouds(
    @SerialName("all") val all: Int? = null
)