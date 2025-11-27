package com.example.matchpet.ui.screens.refugio
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.matchpet.viewmodel.RefugioDashboardViewModel

@Composable
fun RefugioHomeScreen(navController: NavController, token: String, paddingValues: PaddingValues) {
    val viewModel = remember { RefugioDashboardViewModel() }
    val user by viewModel.user.collectAsState()
    val animales by viewModel.animales.collectAsState(initial = emptyList())
    val bitacora by viewModel.bitacora.collectAsState(initial = emptyList())

    // Carga inicial de datos simulados
    LaunchedEffect(token) {
        viewModel.loadUser(token)
        viewModel.loadAnimales(token)
        viewModel.loadBitacora(token)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDFF3FF)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "¡Bienvenido, ${user?.nombreCompleto ?: "Refugio"}!",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF007C91))
                )
                Text(
                    text = "Dashboard de Gestión del Refugio",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF407581))
                )
            }
        }

        item {
            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(title = "Mascotas Disponibles", value = animales.size.toString())
                StatCard(title = "Solicitudes Pendientes", value = "0")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(title = "Adopciones (Mes)", value = "0")
                StatCard(title = "Donaciones (Semana)", value = "$0")
            }
        }

        item {
            Text(
                text = "Últimas Mascotas Agregadas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(animales.takeLast(4).reversed()) { animal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.Gray, RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(animal.nombre, style = MaterialTheme.typography.titleSmall)
                        Text(animal.raza ?: "Desconocida", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Text(
                text = "Bitácora de Actividad Reciente",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(bitacora) { activity ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(activity.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        activity.fecha.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
