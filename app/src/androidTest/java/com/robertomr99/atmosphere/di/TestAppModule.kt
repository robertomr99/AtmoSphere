package com.robertomr99.atmosphere.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.framework.core.FrameworkCoreExtrasModule
import com.robertomr99.atmosphere.framework.core.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import javax.inject.Named
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FrameworkCoreExtrasModule::class]
)

object TestAppModule {

    @Provides
    @Singleton
    fun providesDatabase(app: Application): Database {

        val db = Room.inMemoryDatabaseBuilder(
            app,
            Database::class.java
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .setTransactionExecutor(Dispatchers.Main.asExecutor())
            .allowMainThreadQueries()
            .build()

        return db
    }

    @Provides
    @Singleton
    @Named("apiWeatherUrl")
    fun providesApiWeatherUrl() : String = "http://localhost:8080"

    @Provides
    @Singleton
    @Named("apiGeocodingUrl")
    fun providesApiGeocodingUrl(): String = "http://localhost:8080"

}
