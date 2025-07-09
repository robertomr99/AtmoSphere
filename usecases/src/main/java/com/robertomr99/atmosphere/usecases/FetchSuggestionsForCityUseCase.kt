package com.robertomr99.atmosphere.usecases

import com.robertomr99.atmosphere.domain.CityCoordinatesResponse
import com.robertomr99.atmosphere.data.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow

class FetchSuggestionsForCityUseCase(
    private val repository: WeatherRepository
){
    operator fun invoke(query: String): Flow<List<CityCoordinatesResponse>> = repository.getSuggestionsForCity(query)
}