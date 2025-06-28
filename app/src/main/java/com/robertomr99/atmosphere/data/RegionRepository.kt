package com.robertomr99.atmosphere.data

import com.robertomr99.atmosphere.data.datasource.RegionDataSource


class RegionRepository(private val regionDataSource: RegionDataSource) {
    suspend fun findLastRegion(): String = regionDataSource.findLastRegion()
}

