package com.robertomr99.atmosphere.framework.core

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.domain.weather.IDataStoreManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object FrameworkCoreModule {
    @Provides
    fun providesWeatherDao(database: Database) = database.weatherDao()

    @Provides
    @Singleton
    fun providesWeatherClient(
        @Named("apiKey") apiKey: String,
        @Named("apiWeatherUrl") apiWeatherUrl: String
    ) = WeatherClient(apiKey, apiWeatherUrl).instance

    @Provides
    @Singleton
    fun providesGeoCodingClient(
        @Named("apiKey") apiKey: String,
        @Named("apiGeocodingUrl") apiGeocodingUrl: String
    ) = GeoCodingClient(apiKey, apiGeocodingUrl).instance
}

@Module
@InstallIn(SingletonComponent::class)
object FrameworkCoreExtrasModule {
    @Provides
    @Singleton
    fun providesDatabase(app: Application) = Room
        .databaseBuilder(app, Database::class.java, "db")
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .build()


    @Provides
    @Singleton
    @Named("apiWeatherUrl")
    fun providesApiWeatherUrl(): String = "https://api.openweathermap.org/data/2.5/"

    @Provides
    @Singleton
    @Named("apiGeocodingUrl")
    fun providesApiGeocodingUrl(): String = "https://api.openweathermap.org/geo/1.0/"
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FrameworkCoreBindsModule {

    @Binds
    abstract fun bindDataStore(localDataSource: DataStoreManager) : IDataStoreManager
}
