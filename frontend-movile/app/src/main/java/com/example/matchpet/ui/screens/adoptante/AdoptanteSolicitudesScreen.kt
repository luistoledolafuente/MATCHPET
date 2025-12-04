package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchpet.data.model.SolicitudResponse
import com.example.matchpet.ui.theme.PaleTeal
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.adoptante.AdoptanteSolicitudesViewModel
import com.example.matchpet.viewmodel.adoptante.SolicitudesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdoptanteSolicitudesScreen(
    token: String,
    paddingValues: PaddingValues
) {
    val viewModel: AdoptanteSolicitudesViewModel = viewModel(
        factory = Injection.provideSolicitudesViewModelFactory()
    )

    val state by viewModel.solicitudesState.collectAsState()

    LaunchedEffect(token) {
        viewModel.loadSolicitudes(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes de Adopción", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WebTeal),
                actions = {
                    IconButton(onClick = { viewModel.loadSolicitudes(token) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar", tint = Color.White)
                    }
                }
            )
        },
        containerColor = PaleTeal
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (state) {
                is SolicitudesState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = WebTeal)
                        }
                    }
                }

                is SolicitudesState.Success -> {
                    val solicitudes = (state as SolicitudesState.Success).solicitudes

                    if (solicitudes.isEmpty()) {
                        item {
                            EmptyState()
                        }
                    } else {
                        items(solicitudes) { solicitud ->
                            SolicitudCard(solicitud)
                        }
                    }
                }

                is SolicitudesState.Error -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = (state as SolicitudesState.Error).message,
                                modifier = Modifier.padding(16.dp),
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }

                is SolicitudesState.Idle -> {
                    // Do nothing
                }
            }
        }
    }
}

@Composable
fun SolicitudCard(solicitud: SolicitudResponse) {
    val estadoNombre = solicitud.estadoSolicitud.nombre.uppercase()
    
    // Colores de fondo de tarjeta similares al refugio
    val cardBackgroundColor = when (estadoNombre) {
        "APROBADA", "APROBADO" -> Color(0xFFC8E6C9) // Verde claro
        "RECHAZADA", "RECHAZADO" -> Color(0xFFFFCDD2) // Rojo claro
        else -> Color.White
    }

    // Color del texto/badge del estado
    val estadoTextColor = when (estadoNombre) {
        "APROBADA", "APROBADO" -> Color(0xFF2E7D32)
        "RECHAZADA", "RECHAZADO" -> Color(0xFFC62828)
        "PENDIENTE", "ENVIADA", "EN REVISIÓN" -> Color(0xFFEF6C00)
        else -> Color(0xFF616161)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con foto y nombre
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto de la mascota
                val imageUrl = solicitud.animal.fotos?.firstOrNull()
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = solicitud.animal.nombre,
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Gray, RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF757575)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = solicitud.animal.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF244B57)
                    )

                    Spacer(Modifier.height(4.dp))

                    // Estado
                    Surface(
                        color = if (cardBackgroundColor == Color.White) estadoTextColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = estadoNombre,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = if (cardBackgroundColor == Color.White) estadoTextColor else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Divider()

            Spacer(Modifier.height(12.dp))
            
            // Contenido con mensajes (se expande según el contenido)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Mi mensaje al refugio
                if (!solicitud.mensajeAdoptante.isNullOrBlank()) {
                    Text(
                        text = "Tu mensaje:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF757575)
                    )
                    Spacer(Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = solicitud.mensajeAdoptante,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Mensaje del refugio si existe
                if (!solicitud.mensajeAlAdoptante.isNullOrBlank()) {
                    Text(
                        text = "💬 Respuesta del refugio:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF757575)
                    )
                    Spacer(Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = solicitud.mensajeAlAdoptante,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Pets,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFBDBDBD)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Sin solicitudes por ahora",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF757575)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Explora mascotas disponibles y envía tu primera solicitud de adopción",
                fontSize = 14.sp,
                color = Color(0xFF9E9E9E),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

fun formatDate(dateString: String): String {
    // Simple date formatting - you can enhance this with proper date parsing
    return try {
        dateString.substring(0, 10)
    } catch (e: Exception) {
        dateString
    }
}
