package com.robertomr99.atmosphere.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.robertomr99.atmosphere.domain.weather.TemperatureUnit


@Composable
fun TopBarHomeActions(
    homeState: HomeState,
    onUnitSelected: (TemperatureUnit) -> Unit
) {
    Box {
        IconButton(onClick = { homeState.toggleMenu(true) }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Menu options"
            )
        }

        TemperatureMenu(
            expanded = homeState.menuExpanded,
            selectedUnit = homeState.selectedTemperatureUnit,
            onDismiss = { homeState.toggleMenu(false) },
            onUnitSelected = { unit ->
                onUnitSelected(unit)
                homeState.setTemperatureUnit(unit)
                homeState.toggleMenu(false)
            }
        )
    }
}

@Composable
fun TemperatureMenu(
    expanded: Boolean,
    selectedUnit: TemperatureUnit,
    onDismiss: () -> Unit,
    onUnitSelected: (TemperatureUnit) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        TemperatureMenuItem(
            unit = TemperatureUnit.CELSIUS,
            selectedUnit = selectedUnit,
            label = "Celsius",
            symbol = "°C",
            onClick = { onUnitSelected(TemperatureUnit.CELSIUS) }
        )

        TemperatureMenuDivider()

        TemperatureMenuItem(
            unit = TemperatureUnit.FAHRENHEIT,
            selectedUnit = selectedUnit,
            label = "Fahrenheit",
            symbol = "°F",
            onClick = { onUnitSelected(TemperatureUnit.FAHRENHEIT) }
        )

        TemperatureMenuDivider()

        TemperatureMenuItem(
            unit = TemperatureUnit.KELVIN,
            selectedUnit = selectedUnit,
            label = "Kelvin",
            symbol = "°K",
            onClick = { onUnitSelected(TemperatureUnit.KELVIN) }
        )
    }
}

@Composable
fun TemperatureMenuItem(
    unit: TemperatureUnit,
    selectedUnit: TemperatureUnit,
    label: String,
    symbol: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = symbol,
                    fontWeight = FontWeight.Bold

                )
            }
        },
        leadingIcon = {
            if (unit == selectedUnit) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 4.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        onClick = onClick
    )
}

@Composable
fun TemperatureMenuDivider() {
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .alpha(0.5f),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        thickness = 0.5.dp
    )
}