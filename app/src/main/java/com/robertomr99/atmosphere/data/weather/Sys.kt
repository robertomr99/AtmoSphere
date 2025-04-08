package com.robertomr99.atmosphere.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sys(
    @SerialName("type") val type: Int? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("sunrise") val sunrise: Int? = null,
    @SerialName("sunset") val sunset: Int? = null
)