package com.example.tohtli2.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido a Tohtli", modifier = Modifier.padding(16.dp))

        Button(
            onClick = { navController.navigate("record") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Ir a grabación")
        }

        Button(
            onClick = { navController.navigate("results") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Ver resultados anteriores")
        }
    }
}