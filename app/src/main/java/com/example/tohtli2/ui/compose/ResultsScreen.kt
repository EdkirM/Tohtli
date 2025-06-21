package com.example.tohtli2.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class TranscriptionResult(
    val id: Int,
    val date: String,
    val text: String
)

@Composable
fun ResultsScreen(navController: NavController) {
    // Esto sería reemplazado por datos reales de tu ViewModel
    val results = listOf(
        TranscriptionResult(1, "2023-11-01", "Esta es una transcripción de ejemplo"),
        TranscriptionResult(2, "2023-11-02", "Otra transcripción guardada")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Resultados Anteriores", modifier = Modifier.padding(16.dp))

        LazyColumn {
            items(results) { result ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Fecha: ${result.date}")
                    Text("Texto: ${result.text}")
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