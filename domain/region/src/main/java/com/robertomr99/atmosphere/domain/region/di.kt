package com.robertomr99.atmosphere.domain.region

import com.robertomr99.atmosphere.domain.region.data.RegionRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainRegionModule = module {
    factoryOf(::RegionRepository)
}