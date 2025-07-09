package com.robertomr99.atmosphere.framework.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbWeatherEntity(
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