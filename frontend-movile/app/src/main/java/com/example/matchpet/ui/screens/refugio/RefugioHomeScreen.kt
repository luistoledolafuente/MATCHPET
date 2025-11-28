package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matchpet.viewmodel.ProfileViewModel

@Composable
fun RefugioHomeScreen(navController: NavController, token: String, paddingValues: PaddingValues) {

    val viewModel: ProfileViewModel = viewModel()
    val simpleUser by viewModel.simpleUser.collectAsState(initial = null)
    val animales by viewModel.animales.collectAsState(initial = emptyList())

    // Carga inicial de datos
    LaunchedEffect(token) {
        viewModel.loadUser(token)
        viewModel.loadAnimales(token)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDFF3FF)),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    text = "¡Bienvenido, ${simpleUser?.name ?: "Refugio"}!",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF007C91))
                )
                Text(
                    text = "Dashboard de Gestión del Refugio",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF407581))
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Estadísticas en tarjetas con gradientes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Mascotas Disponibles",
                    value = animales.size.toString(),
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFFFF7E6), Color(0xFFFFE0B2)))
                )
                StatCard(
                    title = "Solicitudes Pendientes",
                    value = "0",
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFD6F0E0), Color(0xFF407581).copy(alpha = 0.3f)))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Adopciones (Mes)",
                    value = "0",
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFDFF3FF), Color(0xFFA8D8E0)))
                )
                StatCard(
                    title = "Donaciones (Semana)",
                    value = "$0",
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFFFE0E0), Color(0xFFFDB2A0)))
                )
            }
        }

        item {
            Text(
                text = "Últimas Mascotas Agregadas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(animales.takeLast(4).reversed()) { animal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Gray, RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(animal.nombre, style = MaterialTheme.typography.titleSmall, color = Color(0xFF007C91))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(animal.raza ?: "Desconocida", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF407581))
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, gradient: Brush) {
    Card(
        modifier = modifier
            .height(100.dp), // Solo altura, peso se aplica afuera
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(title, style = MaterialTheme.typography.bodySmall, color = Color(0xFF244B57))
                Text(value, style = MaterialTheme.typography.titleMedium, color = Color(0xFF244B57))
            }
        }
    }
}
