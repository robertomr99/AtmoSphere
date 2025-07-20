package com.robertomr99.atmosphere.weather.usecases

import com.robertomr99.atmosphere.weather.data.WeatherRepository
import com.robertomr99.atmosphere.weather.entities.WeatherResult
import kotlinx.coroutines.flow.Flow

class FetchFavouritesCitiesUseCase(
    private val repository: WeatherRepository
){
    operator fun invoke(temperatureUnit: String): Flow<List<WeatherResult>> = repository.getWeatherForFavouritesCities(temperatureUnit)
}