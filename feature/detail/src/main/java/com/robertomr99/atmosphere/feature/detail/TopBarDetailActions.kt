package com.robertomr99.atmosphere.feature.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TopBarDetailActions(
    detailState: DetailState,
    onFavClick: (Boolean) -> Unit
) {
    Box {
        IconButton(
            onClick = {
                val newFavoriteState = !detailState.isFavorite
                detailState.setFavoriteCity(newFavoriteState)
                onFavClick(newFavoriteState)
            }
        ) {
            val favIcon = if(detailState.isFavorite){
                Icons.Filled.Favorite
            }else{
                Icons.Filled.FavoriteBorder
            }
            Icon(
                imageVector = favIcon,
                contentDescription = "Fav City Button",
                tint = Color.Red.copy(alpha = 0.8f)
            )
        }
    }
}

