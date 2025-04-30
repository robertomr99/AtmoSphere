package com.robertomr99.atmosphere.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.robertomr99.atmosphere.R
import com.robertomr99.atmosphere.ui.common.PermissionRequestEffect
import java.util.Locale

@Composable
fun SearchFieldWithVoice(
    city: String,
    onCityChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    errorMessage: String? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val ctx = LocalContext.current
    val isError = (!errorMessage.isNullOrEmpty())

    var shouldRequestPermission by remember { mutableStateOf(false) }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            onCityChange(spokenText)
            onSearch(spokenText)
        }
    }

    if (shouldRequestPermission) {
        PermissionRequestEffect(
            permission = Manifest.permission.RECORD_AUDIO,
            onResult = { isGranted ->
                shouldRequestPermission = false
                if (isGranted) {
                    launchVoiceRecognition(ctx, speechRecognizerLauncher)
                } else {
                    Toast.makeText(
                        ctx,
                        "Se necesita permiso de micrófono para la búsqueda por voz",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    OutlinedTextField(
        value = city,
        onValueChange = onCityChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        label = { Text(
                    text ="Buscar ciudad",
                    color = if (isError) Color.Red else Color.White,
                    style =  MaterialTheme.typography.labelMedium,
        )},
        placeholder = { Text(
            text = "Escribe el nombre de la ciudad",
            color = if (isError) Color.Red else Color.White,
            style =  MaterialTheme.typography.bodyMedium,
        )},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Icono de búsqueda",
                tint = Color.White
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (!SpeechRecognizer.isRecognitionAvailable(ctx)) {
                        Toast.makeText(
                            ctx,
                            "El reconocimiento de voz no está disponible en este dispositivo",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@IconButton
                    }

                    shouldRequestPermission = true
                    keyboardController?.hide()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.microphone),
                    contentDescription = "Búsqueda por voz",
                    tint = Color.White
                )
            }
        },

        isError = isError,
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(city)
                keyboardController?.hide()
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = if (isError) Color.Red else Color.White,
            unfocusedTextColor = if (isError) Color.Red else Color.White,
            focusedBorderColor = if (isError) Color.Red else Color(0xFF87CEEB),
            unfocusedBorderColor = if (isError) Color.Red else Color(0xFF87CEEB),
            cursorColor = if (isError) Color.Red else Color.White,
            focusedLabelColor = if (isError) Color.Red else Color.White,
            unfocusedLabelColor = if (isError) Color.Red else Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ),
        textStyle = TextStyle(
            color = if (isError) Color.Red else Color.White
        )
    )
}

private fun launchVoiceRecognition(
    context: Context,
    launcher: ActivityResultLauncher<Intent>
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Dime el nombre de la ciudad que buscas"
        )
    }
    launcher.launch(intent)

    Toast.makeText(context, "Escuchando...", Toast.LENGTH_SHORT).show()
}