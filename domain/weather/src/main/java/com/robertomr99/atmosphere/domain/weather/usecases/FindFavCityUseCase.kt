package com.robertomr99.atmosphere.domain.weather.usecases

import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class FindFavCityUseCase(
    private val repository: WeatherRepository
){
    operator fun invoke(cityName: String, country: String): Flow<Int> = repository.findIfCityIsFav(cityName, country)
}