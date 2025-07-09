package com.robertomr99.atmosphere.domain.forecast

import com.robertomr99.atmosphere.domain.weather.Coord
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class City(
    @SerialName("id") val id : Int? = null,
    @SerialName("name") val name : String? = null,
    @SerialName("coord") val coord : Coord? = null,
    @SerialName("country") val country : String? = null,
    @SerialName("population") val population : Int? = null,
    @SerialName("timezone") val timezone : Int? = null,
    @SerialName("sunrise") val sunrise : Int? = null,
    @SerialName("sunset") val sunset : Int? = null,
)
