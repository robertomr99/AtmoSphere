package com.robertomr99.atmosphere.domain.region.usecases

import com.robertomr99.atmosphere.domain.region.data.RegionRepository
import javax.inject.Inject

class GetCurrentRegionUseCase @Inject constructor(
    private val regionRepository: RegionRepository
) {
    suspend operator fun invoke(): String {
        return try {
            regionRepository.findLastRegion()
        } catch (e: Exception) {
            "ES"
        }
    }
}