package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import kotlinx.coroutines.flow.Flow

class FetchSuggestionsForCityUseCase(
    private val repository: WeatherRepository
){
    operator fun invoke(query: String): Flow<List<CityCoordinatesResponse>> = repository.getSuggestionsForCity(query)
}