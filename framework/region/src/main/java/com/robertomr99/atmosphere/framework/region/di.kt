package com.robertomr99.atmosphere.framework.region

import android.app.Application
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.robertomr99.atmosphere.domain.region.data.LocationDataSource
import com.robertomr99.atmosphere.domain.region.data.RegionDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal abstract class FrameworkRegionBindsModule {

    @Binds
    abstract fun bindLocationDataSource(locationDataSource: PlayServicesLocationDataSource) : LocationDataSource

    @Binds
    abstract fun bindRegionDataSource(regionDataSource: GeocoderRegionDataSource) : RegionDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal class FrameworkRegionModule {

    @Provides
    fun provideFusedLocationProviderClient(app: Application) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }

    @Provides
    fun provideGeocoder(app: Application) : Geocoder {
        return Geocoder(app)
    }
}