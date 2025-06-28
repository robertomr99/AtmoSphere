package com.robertomr99.atmosphere.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertomr99.atmosphere.data.weather.Main
import com.robertomr99.atmosphere.data.weather.Sys
import com.robertomr99.atmosphere.data.weather.Weather
import com.robertomr99.atmosphere.data.weather.WeatherResult
import com.robertomr99.atmosphere.data.weather.Wind

@Entity
data class WeatherEntity(
    @PrimaryKey
    val cityId: String,
    val name: String,
    val country: String,
    val temperatureUnit: String,
    val temp: Double?,
    val maxTemp: Double?,
    val minTemp: Double?,
    val feelsLike: Double?,
    val humidity: Int?,
    val deg: Int?,
    val gust: Double?,
    val speed: Double?,
    val weatherId: Int?,
    val weatherDescription: String?
)

fun toWeatherResult(weather: WeatherEntity): WeatherResult {
    return WeatherResult(
        weather = arrayListOf(Weather(description = weather.weatherDescription, id =  weather.weatherId), ),
        name = weather.name,
        main = Main(
            feelsLike = weather.feelsLike,
            temp = weather.temp,
            tempMin = weather.minTemp,
            tempMax = weather.maxTemp,
            humidity = weather.humidity
        ),
        sys = Sys(
            country = weather.country
        ),
        wind = Wind(
            deg = weather.deg,
            gust = weather.gust,
            speed = weather.speed
        ),
        temperatureUnit = weather.temperatureUnit
    )
}