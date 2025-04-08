package com.robertomr99.atmosphere.data.cityCoord

import kotlinx.serialization.Serializable

@Serializable
data class CityCoordinatesResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null
)