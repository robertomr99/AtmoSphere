package com.robertomr99.atmosphere.data.datasource

import com.robertomr99.atmosphere.domain.Location

interface LocationDataSource {
    suspend fun findLastLocation(): Location?
}