package com.robertomr99.atmosphere.domain.region.usecases

import com.robertomr99.atmosphere.domain.region.data.RegionDataSource
import javax.inject.Inject

class GetCurrentRegionUseCase @Inject constructor(
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