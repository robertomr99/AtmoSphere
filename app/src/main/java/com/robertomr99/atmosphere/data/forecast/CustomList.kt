package com.robertomr99.atmosphere.data.forecast

import com.robertomr99.atmosphere.data.weather.Clouds
import com.robertomr99.atmosphere.data.weather.Sys
import com.robertomr99.atmosphere.data.weather.Weather
import com.robertomr99.atmosphere.data.weather.Wind
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomList(
    @SerialName("dt") val dt: Int? = null,
    @SerialName("main") val main: Main? = null,
    @SerialName("weather") val weather: ArrayList<Weather>? = null,
    @SerialName("clouds") val clouds: Clouds? = null,
    @SerialName("wind") val wind: Wind? = null,
    @SerialName("visibility") val visibility: Int? = null,
    @SerialName("pop") val pop: Double? = null,
    @SerialName("sys") val sys: Sys? = null,
    @SerialName("dtText") val dtText: String? = null,
    @SerialName("rain") val rain: Rain? = null
)
