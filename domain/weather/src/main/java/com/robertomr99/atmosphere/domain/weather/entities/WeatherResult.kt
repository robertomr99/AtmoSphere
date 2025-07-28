package com.robertomr99.atmosphere.domain.weather.entities

import com.robertomr99.atmosphere.domain.weather.entities.weather.Clouds
import com.robertomr99.atmosphere.domain.weather.entities.weather.Coord
import com.robertomr99.atmosphere.domain.weather.entities.weather.Main
import com.robertomr99.atmosphere.domain.weather.entities.weather.Snow
import com.robertomr99.atmosphere.domain.weather.entities.weather.Sys
import com.robertomr99.atmosphere.domain.weather.entities.weather.Weather
import com.robertomr99.atmosphere.domain.weather.entities.weather.Wind
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResult(
    @SerialName("coord") var coord: Coord? = Coord(),
    @SerialName("weather") var weather: ArrayList<Weather?> = arrayListOf(),
    @SerialName("base") var base: String? = null,
    @SerialName("main") var main: Main? = Main(),
    @SerialName("visibility") var visibility: Int? = null,
    @SerialName("wind") var wind: Wind? = Wind(),
    @SerialName("clouds") var clouds: Clouds? = Clouds(),
    @SerialName("dt") var dt: Int? = null,
    @SerialName("sys") var sys: Sys? = Sys(),
    @SerialName("timezone") var timezone: Int? = null,
    @SerialName("id") var id: Int? = null,
    @SerialName("name") var name: String? = null,
    @SerialName("cod") var cod: Int? = null,
    @SerialName("snow") var snow: Snow? = Snow(),
    val temperatureUnit: String ? = null
)

fun WeatherResult.toEntity(cityId: String): WeatherEntity {
    return WeatherEntity(
        cityId = cityId,
        name = name ?: "",
        country = sys?.country ?: "",
        temperatureUnit = this.temperatureUnit ?: "standard",
        temp = main?.temp,
        minTemp = main?.tempMin,
        maxTemp = main?.tempMax,
        feelsLike = main?.feelsLike,
        humidity = main?.humidity,
        speed = wind?.speed,
        deg = wind?.deg,
        gust = wind?.gust,
        weatherId = weather.firstOrNull()?.id,
        weatherDescription = weather.firstOrNull()?.description ?: ""
    )
}


