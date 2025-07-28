package com.robertomr99.atmosphere.domain.region.usecases

import com.robertomr99.atmosphere.domain.region.data.RegionDataSource
import org.koin.core.annotation.Factory

@Factory
class GetCurrentRegionUseCase(
    private val regionDataSource: RegionDataSource
) {
    suspend operator fun invoke(): String {
        return try {
            regionDataSource.findLastRegion()
        } catch (e: Exception) {
            "ES"
        }
    }
}