package com.example.tohtli2.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tohtli2.AudioStorageHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ResultsScreen(navController: NavController) {
    val context = LocalContext.current
    val audioFiles = remember {
        mutableStateOf(AudioStorageHelper.getAllAudioFiles(context))
    }

    val dateFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Resultados Anteriores",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp))

        if (audioFiles.value.isEmpty()) {
            Text("No hay grabaciones disponibles",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn {
                items(audioFiles.value) { audioFile ->
                    val transcription = AudioStorageHelper.getTranscriptionIfExists(audioFile)
                    val date = Date(audioFile.lastModified())

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = dateFormatter.format(date),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (transcription != null) {
                                Text(
                                    text = transcription,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "Transcripción no disponible",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Archivo: ${audioFile.name}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Volver")
        }
    }
}