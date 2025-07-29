package com.robertomr99.atmosphere.framework.weather

import com.robertomr99.atmosphere.domain.weather.data.WeatherLocalDataSource
import com.robertomr99.atmosphere.domain.weather.data.WeatherRemoteDataSource
import com.robertomr99.atmosphere.framework.weather.database.WeatherRoomDataSource
import com.robertomr99.atmosphere.framework.weather.network.WeatherServerDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FrameworkWeatherModule {

    @Binds
    abstract fun bindLocalDataSource (localDataSource: WeatherRoomDataSource) : WeatherLocalDataSource

    @Binds
    abstract fun bindRemoteDataSource (remoteDataSource: WeatherServerDataSource) : WeatherRemoteDataSource
}
