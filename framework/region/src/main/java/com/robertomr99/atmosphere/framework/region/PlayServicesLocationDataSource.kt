package com.robertomr99.atmosphere.framework.region

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.robertomr99.atmosphere.domain.region.data.LocationDataSource
import com.robertomr99.atmosphere.domain.region.entities.Location
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PlayServicesLocationDataSource(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationDataSource {

    override suspend fun findLastLocation(): Location? = fusedLocationClient.lastLocation()
}

@SuppressLint("MissingPermission")
private suspend fun FusedLocationProviderClient.lastLocation(): Location? {
    return suspendCancellableCoroutine { continuation ->
        lastLocation.addOnSuccessListener { location ->
            continuation.resume(location?.toDomainLocation())
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }
}

private fun android.location.Location.toDomainLocation(): Location = Location(latitude, longitude)