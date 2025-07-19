package com.robertomr99.atmosphere.detail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class DetailState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val scrollBehavior : TopAppBarScrollBehavior,
    val scrollState : ScrollState,
    isFavCity: Boolean = false
){

    var isFavorite by mutableStateOf(isFavCity)
        private set

    fun setFavoriteCity(favorite: Boolean) {
        isFavorite = favorite
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDetailState(
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    scrollState: ScrollState = rememberScrollState(),
    isFavCity: Boolean
): DetailState {

    return remember(scrollBehavior, scrollState, isFavCity) { DetailState(scrollBehavior, scrollState, isFavCity) }
}