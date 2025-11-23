package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(token: String) {
    // Aquí podrías obtener user info desde ViewModel usando el token
    val userName = remember { "Adoptante" } // Temporal

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF7E6)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header con saludo
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "¡Hola, $userName! 🐾",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF316B7A))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tu Ubicación: Lima, Perú", // Luego usar ubicación real
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            // Barra de búsqueda
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar mascota por tipo, refugio o raza...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true
            )
        }

        item {
            // Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBAE6FD)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("¡Tu Match Perfecto te Espera!", color = Color(0xFF316B7A))
                }
            }
        }

        item {
            Text("Nuevas Mascotas Cerca de Ti", style = MaterialTheme.typography.titleMedium)
        }

        // Tarjetas de mascotas
        items(4) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Gray, RoundedCornerShape(12.dp))
                    ) {
                        // Aquí luego cargarías imagen con Coil
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text("Max (Perro)", style = MaterialTheme.typography.titleSmall)
                        Text("Refugio San Roque", style = MaterialTheme.typography.bodySmall)
                        Text("3 años", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Text("Refugios Destacados", style = MaterialTheme.typography.titleMedium)
        }

        // Tarjetas de refugios
        items(3) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.LightGray, RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Refugio Esperanza", style = MaterialTheme.typography.titleSmall)
                        Text("Lima, Perú", style = MaterialTheme.typography.bodySmall)
                        Text("★★★★☆", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
