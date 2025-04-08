package com.robertomr99.atmosphere.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Snow(
    @SerialName("d1h") val d1h : Double? = null
)
