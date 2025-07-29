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
    @Singleton
    fun provideDatabase(app: Application) = Room
        .databaseBuilder(app, Database::class.java, "db")
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .build()

    @Provides
    fun providesWeatherDao(database: Database) = database.weatherDao()

    @Provides
    @Singleton
    fun providesWeatherClient(@Named("apiKey") apiKey: String) = WeatherClient(apiKey).instance

    @Provides
    @Singleton
    fun providesGeoCodingClient(@Named("apiKey") apiKey: String) = GeoCodingClient(apiKey).instance
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FrameworkCoreBindsModule {

    @Binds
    abstract fun bindDataStore(localDataSource: DataStoreManager) : IDataStoreManager
}
