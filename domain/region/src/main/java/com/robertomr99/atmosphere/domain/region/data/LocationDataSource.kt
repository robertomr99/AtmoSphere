package com.robertomr99.atmosphere.domain.region.data

import com.robertomr99.atmosphere.domain.region.entities.Location

interface LocationDataSource {
    suspend fun findLastLocation(): Location?
}