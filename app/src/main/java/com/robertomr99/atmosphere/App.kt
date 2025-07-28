package com.robertomr99.atmosphere

import android.app.Application
import com.robertomr99.atmosphere.domain.region.DomainRegionModule
import com.robertomr99.atmosphere.domain.weather.DomainWeatherModule
import com.robertomr99.atmosphere.domain.weather.Logger
import com.robertomr99.atmosphere.feature.detail.FeatureDetailModule
import com.robertomr99.atmosphere.feature.home.FeatureHomeModule
import com.robertomr99.atmosphere.framework.core.FrameworkCoreModule
import com.robertomr99.atmosphere.framework.core.frameworkCoreModule
import com.robertomr99.atmosphere.framework.region.FrameworkRegionModule
import com.robertomr99.atmosphere.framework.region.frameworkRegionModule
import com.robertomr99.atmosphere.framework.weather.FrameworkWeatherModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.module

class App : Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                appModule,
                FeatureHomeModule().module,
                FeatureDetailModule().module,
                DomainWeatherModule().module,
                DomainRegionModule().module,
                FrameworkCoreModule().module,
                frameworkCoreModule,
                FrameworkWeatherModule().module,
                FrameworkRegionModule().module,
                frameworkRegionModule
            )
        }

    }
}

val appModule = module {
    single(named("apiKey")) { BuildConfig.OW_API_KEY }
    single<Logger> { AndroidLogger() }
}