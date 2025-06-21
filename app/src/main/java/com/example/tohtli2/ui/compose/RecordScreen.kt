package com.example.tohtli2.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RecordScreen(navController: NavController) {
    val context = LocalContext.current
    val isRecording = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pantalla de Grabación", modifier = Modifier.padding(16.dp))

        Button(
            onClick = {
                isRecording.value = !isRecording.value
                // Aquí iría la lógica para iniciar/detener la grabación
                // Similar a tu implementación actual en MainActivity
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(if (isRecording.value) "Detener Grabación" else "Iniciar Grabación")
        }

        Button(
            onClick = { navController.navigate("results") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Ver Transcripción")
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Volver")
        }
    }
}