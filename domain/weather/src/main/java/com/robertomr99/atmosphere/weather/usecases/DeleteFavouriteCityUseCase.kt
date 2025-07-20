package com.robertomr99.atmosphere.weather.usecases

import com.robertomr99.atmosphere.weather.data.WeatherRepository

class DeleteFavouriteCityUseCase(
    private val repository: WeatherRepository
){
    suspend operator fun invoke(cityName: String, country: String): Unit = repository.deleteFavouriteCity(cityName, country)
}