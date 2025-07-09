package com.robertomr99.atmosphere.usecases

import com.robertomr99.atmosphere.domain.WeatherResult
import com.robertomr99.atmosphere.data.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class FetchFavouritesCitiesUseCase(
    private val repository: WeatherRepository
){
    operator fun invoke(temperatureUnit: String): Flow<List<WeatherResult>> = repository.getWeatherForFavouritesCities(temperatureUnit)
}