package com.robertomr99.atmosphere.region

import android.location.Geocoder
import com.robertomr99.atmosphere.region.data.DEFAULT_REGION
import com.robertomr99.atmosphere.region.data.LocationDataSource
import com.robertomr99.atmosphere.region.data.RegionDataSource
import com.robertomr99.atmosphere.region.entities.Location

class GeocoderRegionDataSource(
    private val geocoder: Geocoder,
    private val locationDataSource: LocationDataSource
) : RegionDataSource {

    override suspend fun findLastRegion(): String =
        locationDataSource.findLastLocation()?.toRegion() ?: DEFAULT_REGION

    private suspend fun Location.toRegion(): String {
        val addresses = geocoder.getFromLocationCompat(latitude, longitude, 1)
        val region = addresses.firstOrNull()?.countryCode
        return region ?: DEFAULT_REGION
    }

}