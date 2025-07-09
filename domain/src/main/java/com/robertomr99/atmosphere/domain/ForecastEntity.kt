package com.robertomr99.atmosphere.domain

data class ForecastEntity(
    val id: Int = 0,
    val cityOwnerId: String,
    val hour: Int,
    val temp: Int,
    val tempMin: Int,
    val tempMax: Int,
    val weatherIcon: String
)

