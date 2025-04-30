package com.robertomr99.atmosphere.ui.screens.detail

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
){

    var isFavorite by mutableStateOf(false)
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
): DetailState{

    return remember(scrollBehavior, scrollState) { DetailState(scrollBehavior, scrollState) }
}