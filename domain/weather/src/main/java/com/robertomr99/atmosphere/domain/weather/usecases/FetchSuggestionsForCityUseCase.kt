package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import com.robertomr99.atmosphere.domain.weather.entities.CityCoordinatesResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchSuggestionsForCityUseCase @Inject constructor(
    private val repository: WeatherRepository
){
    operator fun invoke(query: String): Flow<List<CityCoordinatesResponse>> = repository.getSuggestionsForCity(query)
}