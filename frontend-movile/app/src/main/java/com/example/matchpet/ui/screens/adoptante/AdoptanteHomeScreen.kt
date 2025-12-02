package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matchpet.data.model.animal.MascotaCardData
import com.example.matchpet.data.model.SolicitudData
import com.example.matchpet.ui.theme.PaleTeal
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.viewmodel.adoptante.AdoptanteHomeViewModel
import com.example.matchpet.viewmodel.adoptante.DashboardState

@Composable
fun AdoptanteHomeScreen(navController: NavController, token: String, paddingValues: PaddingValues) {

    val viewModel: AdoptanteHomeViewModel = viewModel()
    val dashboardState by viewModel.dashboardState.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(token) {
        viewModel.loadDashboardStats(token)
    }

    // Datos de ejemplo para las recomendaciones
    val recomendaciones = listOf(
        MascotaCardData("Max", "Mestizo", "2 años", "Refugio San Roque"),
        MascotaCardData("Luna", "Labrador", "1 año", "Refugio Esperanza"),
        MascotaCardData("Rocky", "Pastor Alemán", "3 años", "Patitas Felices"),
        MascotaCardData("Bella", "Golden Retriever", "4 años", "Refugio Norte")
    )

    val nuevasMascotas = listOf(
        MascotaCardData("Toby", "Bulldog", "5 años", "Hogar Animal"),
        MascotaCardData("Kira", "Siames", "1 año", "Refugio Esperanza")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PaleTeal),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // --- HEADER ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "¡Hola, Adoptante! 🐾",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40)
                    )
                    Text(
                        text = "Dashboard de actividad y mascotas disponibles 💛",
                        fontSize = 13.sp,
                        color = Color(0xFF00695C)
                    )
                }
            }
        }

        // --- ESTADÍSTICAS ---
        item {
            when (dashboardState) {
                is DashboardState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WebTeal)
                    }
                }
                is DashboardState.Success -> {
                    val stats = (dashboardState as DashboardState.Success).stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard("Pendientes", stats.solicitudesPendientes.toString(), Color(0xFFEF6C00))
                        StatCard("Aprobadas", stats.adopcionesAprobadas.toString(), Color(0xFF2E7D32))
                        StatCard("Donaciones", "$${stats.totalDonaciones}", Color(0xFFC62828))
                    }
                }
                is DashboardState.Error -> {
                    Text(
                        text = (dashboardState as DashboardState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    // Idle state - mostrar valores por defecto
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard("Pendientes", "0", Color(0xFFEF6C00))
                        StatCard("Aprobadas", "0", Color(0xFF2E7D32))
                        StatCard("Donaciones", "$0", Color(0xFFC62828))
                    }
                }
            }
        }

        // --- BUSCADOR ---
        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar mascota por tipo, refugio o raza…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = WebTeal,
                    unfocusedBorderColor = Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // --- RECOMENDADAS ---
        item { SectionTitle("Recomendadas Para Ti", Modifier.padding(start = 16.dp)) }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(recomendaciones) { MascotaCard(it) { navController.navigate("adoptante_mascotas") } }
            }
        }

        // --- NUEVAS ---
        item { SectionTitle("Nuevas Cerca de Ti", Modifier.padding(start = 16.dp)) }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(nuevasMascotas) { MascotaCard(it) { navController.navigate("adoptante_mascotas") } }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---
@Composable
fun StatCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .height(80.dp)
            .width(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.Gray, maxLines = 1)
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40), modifier = modifier)
}

@Composable
fun MascotaCard(item: MascotaCardData, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(160.dp).height(240.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(PaleTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = WebTeal, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF004D40))
            Text("${item.raza}, ${item.edad}", fontSize = 12.sp, color = Color.Gray)
            Text(item.refugio, fontSize = 11.sp, color = WebTeal)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onClick,
                modifier = Modifier.height(32.dp).fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                contentPadding = PaddingValues(0.dp)
            ) { Text("Ver Perfil", fontSize = 12.sp) }
        }
    }
}
