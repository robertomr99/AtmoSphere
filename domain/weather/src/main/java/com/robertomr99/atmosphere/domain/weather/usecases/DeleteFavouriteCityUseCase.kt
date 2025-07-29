package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import javax.inject.Inject

class DeleteFavouriteCityUseCase @Inject constructor(
    private val repository: WeatherRepository
){
    suspend operator fun invoke(cityName: String, country: String): Unit = repository.deleteFavouriteCity(cityName, country)
}