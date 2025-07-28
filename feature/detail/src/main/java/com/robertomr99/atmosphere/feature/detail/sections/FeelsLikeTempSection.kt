package com.robertomr99.atmosphere.feature.detail.sections

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertomr99.atmosphere.feature.common.CompactSectionBackground

@SuppressLint("DefaultLocale")
@Composable
fun FeelsLikeTempSection(
    feelsLikeTemp : Int?,
    modifier: Modifier
) {
    CompactSectionBackground(modifier){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sensación",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${feelsLikeTemp.toString()}°",
                color = Color.White,
                fontSize = 20.sp,
            )
            }
        }
    }