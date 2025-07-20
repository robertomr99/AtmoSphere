package com.robertomr99.atmosphere.weather.usecases

import com.robertomr99.atmosphere.weather.data.WeatherRepository
import kotlinx.coroutines.flow.Flow

class FindFavCityUseCase(
    private val repository: WeatherRepository
){
    operator fun invoke(cityName: String, country: String): Flow<Int> = repository.findIfCityIsFav(cityName, country)
}