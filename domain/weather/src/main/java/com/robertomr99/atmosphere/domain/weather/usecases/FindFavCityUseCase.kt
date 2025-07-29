package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindFavCityUseCase @Inject constructor(
    private val repository: WeatherRepository
){
    operator fun invoke(cityName: String, country: String): Flow<Int> = repository.findIfCityIsFav(cityName, country)
}