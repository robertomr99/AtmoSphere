package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import com.robertomr99.atmosphere.domain.weather.entities.WeatherResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchFavouritesCitiesUseCase @Inject constructor(
    private val repository: WeatherRepository
){
    operator fun invoke(temperatureUnit: String): Flow<List<WeatherResult>> = repository.getWeatherForFavouritesCities(temperatureUnit)
}