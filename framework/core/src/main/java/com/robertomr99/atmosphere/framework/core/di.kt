package com.robertomr99.atmosphere.framework.core

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module


@Module
@ComponentScan
class FrameworkCoreModule

val frameworkCoreModule = module {
    single { Room
        .databaseBuilder(get(), Database::class.java, "db")
        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .build()
    }
    factory{ get<Database>().weatherDao() }
    single { WeatherClient(get(named("apiKey"))).instance }
    single { GeoCodingClient(get(named("apiKey"))).instance }
}