package com.robertomr99.atmosphere.weather.entities.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rain(
    @SerialName("3h") val threeHours: Double? = null
)