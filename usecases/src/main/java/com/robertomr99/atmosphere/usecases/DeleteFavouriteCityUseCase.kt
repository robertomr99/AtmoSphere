package com.robertomr99.atmosphere.usecases

import com.robertomr99.atmosphere.data.repository.WeatherRepository

class DeleteFavouriteCityUseCase(
    private val repository: WeatherRepository
){
    suspend operator fun invoke(cityName: String, country: String): Unit = repository.deleteFavouriteCity(cityName, country)
}