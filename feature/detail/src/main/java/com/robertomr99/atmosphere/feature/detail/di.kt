package com.robertomr99.atmosphere.feature.detail

import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val featureDetailModule = module {
    viewModelOf(::DetailViewModel)
}