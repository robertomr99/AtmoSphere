package com.robertomr99.atmosphere.data.datasource
const val DEFAULT_REGION = "ES"

interface RegionDataSource {
    suspend fun findLastRegion(): String
}