package com.robertomr99.atmosphere.domain.region.data
const val DEFAULT_REGION = "ES"

interface RegionDataSource {
    suspend fun findLastRegion(): String
}