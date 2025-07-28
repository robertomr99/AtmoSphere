package com.robertomr99.atmosphere.domain.weather

import com.robertomr99.atmosphere.domain.region.usecases.GetCurrentRegionUseCase
import com.robertomr99.atmosphere.domain.weather.data.WeatherRepository
import com.robertomr99.atmosphere.domain.weather.usecases.DeleteFavouriteCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchFavouritesCitiesUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchSuggestionsForCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FetchWeatherAndForecastUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.FindFavCityUseCase
import com.robertomr99.atmosphere.domain.weather.usecases.SaveFavouriteCityUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainWeatherModule = module {
    factoryOf(::WeatherRepository)
    factoryOf(::DeleteFavouriteCityUseCase)
    factoryOf(::FetchFavouritesCitiesUseCase)
    factoryOf(::FetchSuggestionsForCityUseCase)
    factoryOf(::FetchWeatherAndForecastUseCase)
    factoryOf(::FindFavCityUseCase)
    factoryOf(::SaveFavouriteCityUseCase)
    factoryOf(::GetCurrentRegionUseCase)
}