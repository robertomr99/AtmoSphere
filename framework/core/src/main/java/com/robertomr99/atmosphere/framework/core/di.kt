package com.robertomr99.atmosphere.framework.core

import androidx.room.Room
import androidx.room.RoomDatabase
import com.robertomr99.atmosphere.domain.weather.IDataStoreManager
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val frameworkCoreModule = module {
    single { Room
        .databaseBuilder(get(), Database::class.java, "db")
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .build()
    }
    factory{ get<Database>().weatherDao() }
    factoryOf(::DataStoreManager) bind IDataStoreManager::class
    single { WeatherClient(get(named("apiKey"))).instance }
    single { GeoCodingClient(get(named("apiKey"))).instance }

}