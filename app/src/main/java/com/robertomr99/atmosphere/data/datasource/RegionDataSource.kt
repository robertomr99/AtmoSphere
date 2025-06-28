package com.robertomr99.atmosphere.data.datasource

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

const val DEFAULT_REGION = "ES"

class RegionDataSource(app : Application, private val locationDataSource: LocationDataSource) {
    private val geoCoder = Geocoder(app)
    suspend fun findLastRegion(): String = locationDataSource.findLastLocation()?.toRegion() ?: DEFAULT_REGION

    private suspend fun Location.toRegion(): String {
        val addresses = geoCoder.getFromLocationCompat(latitude, longitude, 1)
        val region = addresses.firstOrNull()?.countryCode
        return region ?: DEFAULT_REGION
    }
}

@Suppress("DEPRECATION")
private suspend fun Geocoder.getFromLocationCompat(
    @FloatRange(from = -90.0, to = 90.0) latitude: Double,
    @FloatRange(from = -180.0, to = 180.0) longitude: Double,
    @IntRange maxResults: Int
): List<Address> = if (Build.VERSION.SDK_INT >=
    Build.VERSION_CODES.TIRAMISU) {
    suspendCancellableCoroutine { continuation ->
        getFromLocation(latitude, longitude, maxResults) {
            continuation.resume(it)
        }
    }
} else {
    withContext(Dispatchers.IO) {
        getFromLocation(latitude, longitude, maxResults) ?: emptyList()
    }
}