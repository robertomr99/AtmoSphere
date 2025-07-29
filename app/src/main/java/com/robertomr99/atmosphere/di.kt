package com.robertomr99.atmosphere

import com.robertomr99.atmosphere.domain.weather.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    @Singleton
    @Named("apiKey")
    fun provideApiKey() = BuildConfig.OW_API_KEY

    @Provides
    @Singleton
    fun providesLogger() : Logger = AndroidLogger()

}