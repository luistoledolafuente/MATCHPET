package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.matchpet.data.model.animal.Animal
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(token) {
        viewModel.loadDashboardStats(token)
    }

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
                    val data = (dashboardState as DashboardState.Success).data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard("Pendientes", data.stats.solicitudesPendientes.toString(), Color(0xFFEF6C00))
                        StatCard("Aprobadas", data.stats.adopcionesAprobadas.toString(), Color(0xFF2E7D32))
                        StatCard("Donaciones", "$${data.stats.totalDonaciones}", Color(0xFFC62828))
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
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Buscar mascota por tipo, refugio o raza…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
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

        // --- RESULTADOS DE BÚSQUEDA O DASHBOARD ---
        if (searchQuery.isNotEmpty()) {
            if (searchResults.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No se encontraron mascotas", color = Color.Gray)
                    }
                }
            } else {
                // Mostrar resultados en Grid (usando LazyColumn items con Rows o FlowRow, pero aquí usaremos items simples para la lista vertical por ahora, o mejor, un Grid adaptado)
                // Como estamos dentro de un LazyColumn, no podemos meter un LazyVerticalGrid directamente sin altura fija.
                // Una solución simple es renderizar filas de 2 elementos manualmente o usar FlowRow (si está disponible y estable).
                // Para simplificar y mantener el scroll de la pantalla completa, renderizaremos filas de 2 items.
                
                val rows = searchResults.chunked(2)
                items(rows) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (animal in rowItems) {
                            RealMascotaCard(animal) {
                                navController.navigate("animal_detail/${animal.animal_id}")
                            }
                        }
                    }
                }
            }
        } else {
            // --- RECOMENDADAS PARA TI (Mascotas aleatorias) ---
            when (val state = dashboardState) {
                is DashboardState.Success -> {
                    if (state.data.mascotasRecomendadas.isNotEmpty()) {
                        item { SectionTitle("Recomendadas Para Ti", Modifier.padding(start = 16.dp)) }
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(state.data.mascotasRecomendadas) { animal ->
                                    RealMascotaCard(animal) { 
                                        navController.navigate("animal_detail/${animal.animal_id}") 
                                    }
                                }
                            }
                        }
                    }
    
                    // --- TUS SOLICITUDES (Mascotas solicitadas) ---
                    if (state.data.mascotasSolicitadas.isNotEmpty()) {
                        item { SectionTitle("Tus Solicitudes", Modifier.padding(start = 16.dp)) }
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(state.data.mascotasSolicitadas) { animal ->
                                    RealMascotaCard(animal) { 
                                        navController.navigate("animal_detail/${animal.animal_id}") 
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {
                    // Mostrar loading o placeholder
                }
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

@Composable
fun RealMascotaCard(animal: Animal, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(160.dp).height(240.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Imagen de la mascota
            if (!animal.fotos.isNullOrEmpty()) {
                coil.compose.AsyncImage(
                    model = "http://10.0.2.2:8081${animal.fotos[0]}",
                    contentDescription = animal.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(PaleTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(PaleTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Pets, contentDescription = null, tint = WebTeal, modifier = Modifier.size(32.dp))
                }
            }
            
            Spacer(Modifier.height(10.dp))
            Text(animal.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF004D40), maxLines = 1)
            Text(
                text = "${animal.raza ?: "Mestizo"}${if (animal.genero != null) " • ${animal.genero}" else ""}",
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1
            )
            Text(
                text = animal.refugioNombre ?: "Refugio",
                fontSize = 11.sp,
                color = WebTeal,
                maxLines = 1
            )
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

