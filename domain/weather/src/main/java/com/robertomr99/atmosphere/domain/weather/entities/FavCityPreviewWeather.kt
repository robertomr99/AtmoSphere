package com.robertomr99.atmosphere.domain.weather.entities

data class FavCityPreviewWeather(
        val name: String,
        val country: String,
        val weatherId: Int,
        val temp: String,
        val minTemp: String,
        val maxTemp: String,
        val description: String
)