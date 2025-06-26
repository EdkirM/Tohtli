package com.example.tohtli2.ui.compose

import android.Manifest
import android.content.pm.PackageManager
import android.content.Context
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tohtli2.AudioStorageHelper
import com.example.tohtli2.WhisperApiService
import com.example.tohtli2.createWhisperService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(navController: NavController) {
    val context = LocalContext.current
    val isRecording = remember { mutableStateOf(false) }
    val audioFile = remember { mutableStateOf<File?>(null) }
    val whisperService = remember { createWhisperService() }
    val scope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val mediaRecorder = remember {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    // Estados para traducción
    val targetLanguage = remember { mutableStateOf("es") }
    val availableLanguages = listOf(
        "es" to "Español",
        "en" to "Inglés",
        "de" to "Alemán",
        "fr" to "Francés",
        "it" to "Italiano",
        "zh" to "Chino Mandarín",
        "ja" to "Japonés",
        "ko" to "Coreano"
    )
    val expandedLanguageMenu = remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            errorMessage.value = "Permiso de grabación denegado"
        }
    }

    fun checkAndRequestPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permiso ya concedido
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    fun createAudioFile(context: Context): File {
        return AudioStorageHelper.createNewAudioFile(context)
    }

    if (errorMessage.value != null) {
        AlertDialog(
            onDismissRequest = { errorMessage.value = null },
            title = { Text("Error") },
            text = { Text(errorMessage.value!!) },
            confirmButton = {
                Button(onClick = { errorMessage.value = null }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla de Grabación", style = MaterialTheme.typography.headlineSmall)

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                checkAndRequestPermissions()
                try {
                    isRecording.value = !isRecording.value
                    if (isRecording.value) {
                        audioFile.value = createAudioFile(context)
                        startRecording(mediaRecorder, audioFile.value!!)
                    } else {
                        stopRecording(mediaRecorder)
                    }
                } catch (e: Exception) {
                    errorMessage.value = "Error al grabar: ${e.localizedMessage}"
                    isRecording.value = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(if (isRecording.value) "Detener Grabación" else "Iniciar Grabación")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de idioma para traducción
        ExposedDropdownMenuBox(
            expanded = expandedLanguageMenu.value,
            onExpandedChange = { expandedLanguageMenu.value = !expandedLanguageMenu.value }
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = availableLanguages.find { it.first == targetLanguage.value }?.second ?: "",
                onValueChange = {},
                label = { Text("Idioma de Traducción") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLanguageMenu.value)
                }
            )

            ExposedDropdownMenu(
                expanded = expandedLanguageMenu.value,
                onDismissRequest = { expandedLanguageMenu.value = false }
            ) {
                availableLanguages.forEach { (code, name) ->
                    DropdownMenuItem(
                        text = { Text(name) },
                        onClick = {
                            targetLanguage.value = code
                            expandedLanguageMenu.value = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                audioFile.value?.let { file ->
                    isLoading.value = true
                    scope.launch {
                        try {
                            // 1. Transcribir el audio
                            val requestFile = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
                            val audioPart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                            val model = "whisper-1".toRequestBody("text/plain".toMediaTypeOrNull())

                            val transcriptionResponse = whisperService.transcribeAudio(audioPart, model)

                            if (transcriptionResponse.isSuccessful) {
                                val transcriptionText = transcriptionResponse.body()?.text ?: "Sin texto"

                                // Guardar la transcripción
                                AudioStorageHelper.saveTranscription(file, transcriptionText)

                                // 2. Traducir automáticamente al idioma seleccionado
                                val targetLanguageName = availableLanguages.find { it.first == targetLanguage.value }?.second ?: ""
                                val translationRequest = com.example.tohtli2.TranslationRequest(
                                    messages = listOf(
                                        com.example.tohtli2.Message(
                                            content = "Traduce el siguiente texto a $targetLanguageName manteniendo el mismo formato: $transcriptionText"
                                        )
                                    )
                                )

                                val translationResponse = whisperService.translateText(translationRequest)

                                if (translationResponse.isSuccessful) {
                                    val translatedText = translationResponse.body()?.choices?.firstOrNull()?.message?.content ?: "Sin traducción"

                                    // Guardar la traducción
                                    AudioStorageHelper.saveTranslation(file, targetLanguage.value, translatedText)

                                    // Navegar a resultados
                                    navController.navigate("results")
                                } else {
                                    errorMessage.value = "Error en traducción: ${translationResponse.code()}"
                                }
                            } else {
                                errorMessage.value = "Error en transcripción: ${transcriptionResponse.code()}"
                            }
                        } catch (e: Exception) {
                            errorMessage.value = "Error: ${e.message}"
                        } finally {
                            isLoading.value = false
                        }
                    }
                } ?: run {
                    errorMessage.value = "No hay audio grabado"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            enabled = audioFile.value != null && !isRecording.value && !isLoading.value
        ) {
            Text("Transcribir y Traducir")
        }

        Spacer(modifier = Modifier.height(16.dp))

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

private fun startRecording(mediaRecorder: MediaRecorder, outputFile: File) {
    try {
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(192000)
            }

            prepare()
            start()
        }
    } catch (e: Exception) {
        throw IllegalStateException("Error al iniciar la grabación", e)
    }
}

private fun stopRecording(mediaRecorder: MediaRecorder) {
    try {
        mediaRecorder.stop()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            mediaRecorder.reset()
            mediaRecorder.release()
        } else {
            @Suppress("DEPRECATION")
            mediaRecorder.reset()
        }
    } catch (e: IllegalStateException) {
        throw IllegalStateException("Error al detener la grabación", e)
    }
}