package com.example.tohtli2.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tohtli2.AudioStorageHelper
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(navController: NavController) {
    val context = LocalContext.current
    val audioFiles = remember { mutableStateOf(AudioStorageHelper.getAllAudioFiles(context)) }
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Resultados",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            if (audioFiles.value.isEmpty()) {
                Text(
                    "No hay grabaciones disponibles",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(audioFiles.value) { audioFile ->
                        val transcription = AudioStorageHelper.getTranscriptionIfExists(audioFile)
                        val translations = AudioStorageHelper.getAllTranslations(audioFile)
                        val date = Date(audioFile.lastModified())

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Encabezado con fecha
                                Text(
                                    text = dateFormatter.format(date),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Sección de Transcripción
                                Text(
                                    text = "Transcripción:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = transcription ?: "No disponible",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                // Botón para copiar transcripción
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            transcription?.let {
                                                clipboardManager.setText(AnnotatedString(it))
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Transcripción copiada")
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, "Copiar transcripción")
                                    }
                                }

                                // Sección de Traducciones
                                if (translations.isNotEmpty()) {
                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    Text(
                                        text = "Traducciones:",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    translations.forEach { (langCode, translation) ->
                                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                            // Idioma de la traducción
                                            Text(
                                                text = "Idioma: ${getLanguageName(langCode)}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Texto traducido
                                            Text(
                                                text = translation,
                                                style = MaterialTheme.typography.bodyMedium
                                            )

                                            // Botón para copiar traducción
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(translation))
                                                        coroutineScope.launch {
                                                            snackbarHostState.showSnackbar("Traducción copiada")
                                                        }
                                                    }
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, "Copiar traducción")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Volver")
            }
        }
    }
}

private fun getLanguageName(code: String): String {
    return when (code) {
        "es" -> "Español"
        "en" -> "Inglés"
        "de" -> "Alemán"
        "fr" -> "Francés"
        "it" -> "Italiano"
        "zh" -> "Chino Mandarín"
        "ja" -> "Japonés"
        "ko" -> "Coreano"
        else -> code
    }
}