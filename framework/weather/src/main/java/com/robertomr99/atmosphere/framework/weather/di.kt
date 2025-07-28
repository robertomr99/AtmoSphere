package com.robertomr99.atmosphere.framework.weather

import com.robertomr99.atmosphere.domain.weather.data.WeatherLocalDataSource
import com.robertomr99.atmosphere.domain.weather.data.WeatherRemoteDataSource
import com.robertomr99.atmosphere.framework.weather.database.WeatherRoomDataSource
import com.robertomr99.atmosphere.framework.weather.network.WeatherServerDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val frameworkWeatherModule = module {
    factoryOf(::WeatherRoomDataSource) bind WeatherLocalDataSource::class
    factoryOf(::WeatherServerDataSource) bind WeatherRemoteDataSource::class
}