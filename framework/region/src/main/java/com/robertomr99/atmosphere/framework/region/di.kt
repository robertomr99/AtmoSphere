package com.robertomr99.atmosphere.framework.region

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.robertomr99.atmosphere.domain.region.data.LocationDataSource
import com.robertomr99.atmosphere.domain.region.data.RegionDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val frameworkRegionModule = module {
    factoryOf(::PlayServicesLocationDataSource) bind LocationDataSource::class
    factory{ LocationServices.getFusedLocationProviderClient(get<Context>()) }
    factoryOf(::GeocoderRegionDataSource) bind RegionDataSource::class
    factory { Geocoder(get()) }
}