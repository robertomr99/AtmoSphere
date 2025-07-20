package com.robertomr99.atmosphere.region.data

import com.robertomr99.atmosphere.region.entities.Location

interface LocationDataSource {
    suspend fun findLastLocation(): Location?
}