package com.example.tohtli2.ui.compose

import android.Manifest
import android.content.pm.PackageManager
import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.tohtli2.WhisperApiService
import com.example.tohtli2.createWhisperService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

@Composable
fun RecordScreen(navController: NavController) {
    val context = LocalContext.current
    val isRecording = remember { mutableStateOf(false) }
    val audioFile = remember { mutableStateOf<File?>(null) }
    val whisperService = remember { createWhisperService() }
    val scope = rememberCoroutineScope()
    val transcriptionResult = remember { mutableStateOf<String?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val mediaRecorder = remember { MediaRecorder() }

    // Verificación de permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            errorMessage.value = "Permiso de grabación denegado"
        }
    }

    // Función para verificar y solicitar permisos
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

    // Función para crear archivo de audio
    fun createAudioFile(context: Context): File {
        return try {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            File.createTempFile(
                "audio_${System.currentTimeMillis()}",
                ".mp4",
                storageDir
            ).apply {
                createNewFile()
            }
        } catch (e: Exception) {
            throw IOException("No se pudo crear el archivo de audio", e)
        }
    }

    // Mostrar diálogos
    if (transcriptionResult.value != null) {
        AlertDialog(
            onDismissRequest = { transcriptionResult.value = null },
            title = { Text("Transcripción") },
            text = { Text(transcriptionResult.value ?: "") },
            confirmButton = {
                Button(onClick = {
                    transcriptionResult.value = null
                    navController.navigate("results")
                }) {
                    Text("OK")
                }
            }
        )
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
        Text("Pantalla de Grabación", modifier = Modifier.padding(16.dp))

        if (isLoading.value) {
            CircularProgressIndicator()
        }

        Button(
            onClick = {
                checkAndRequestPermissions() // Verificar permisos antes de grabar
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
            modifier = Modifier.padding(8.dp)
        ) {
            Text(if (isRecording.value) "Detener Grabación" else "Iniciar Grabación")
        }

        Button(
            onClick = {
                audioFile.value?.let { file ->
                    isLoading.value = true
                    scope.launch {
                        try {
                            val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
                            val audioPart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                            val model = "whisper-1".toRequestBody("text/plain".toMediaTypeOrNull())

                            val response = whisperService.transcribeAudio(audioPart, model)
                            if (response.isSuccessful) {
                                transcriptionResult.value = response.body()?.text ?: "Sin texto"
                            } else {
                                transcriptionResult.value = "Error: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            transcriptionResult.value = "Error: ${e.message}"
                        } finally {
                            isLoading.value = false
                        }
                    }
                } ?: run {
                    transcriptionResult.value = "No hay audio grabado"
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Transcribir Audio")
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(8.dp)
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
    } catch (e: IllegalStateException) {
        throw IllegalStateException("Error al detener la grabación", e)
    } finally {
        mediaRecorder.reset()
    }
}