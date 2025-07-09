package com.robertomr99.atmosphere.domain

import kotlinx.serialization.Serializable

@Serializable
data class CityCoordinatesResponse(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null,
    val local_names: Map<String, String>? = null
)

fun CityCoordinatesResponse.toRegionLanguage(region: String): CityCoordinatesResponse {
    val regionLanguageName = local_names?.get(region.lowercase()) ?: name

    return this.copy(
        name = regionLanguageName
    )
}