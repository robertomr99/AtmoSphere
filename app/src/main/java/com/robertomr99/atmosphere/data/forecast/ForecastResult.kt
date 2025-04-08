package com.robertomr99.atmosphere.data.forecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResult (
    @SerialName("cod") val cod: String? = null,
    @SerialName("message") val message: Int? = null,
    @SerialName("cnt") val cnt: Int? = null,
    @SerialName("list") val list: List<CustomList>? = null,
    @SerialName("city") val city: City? = null
)