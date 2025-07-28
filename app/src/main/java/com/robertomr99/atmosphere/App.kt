package com.robertomr99.atmosphere

import android.app.Application
import com.robertomr99.atmosphere.framework.core.frameworkCoreModule
import com.robertomr99.atmosphere.feature.detail.featureDetailModule
import com.robertomr99.atmosphere.feature.home.featureHomeModule
import com.robertomr99.atmosphere.domain.region.domainRegionModule
import com.robertomr99.atmosphere.domain.weather.Logger
import com.robertomr99.atmosphere.framework.region.frameworkRegionModule
import com.robertomr99.atmosphere.domain.weather.domainWeatherModule
import com.robertomr99.atmosphere.framework.weather.frameworkWeatherModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module

class App : Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                appModule,
                featureHomeModule,
                featureDetailModule,
                domainWeatherModule,
                domainRegionModule,
                frameworkCoreModule,
                frameworkWeatherModule,
                frameworkRegionModule
            )
        }

    }
}

val appModule = module {
    single(named("apiKey")) { BuildConfig.OW_API_KEY }
    single<Logger> { AndroidLogger() }
}