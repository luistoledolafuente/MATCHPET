package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Pets
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
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.adoptante.AdoptanteSolicitudesViewModel
import com.example.matchpet.viewmodel.adoptante.SolicitudesState

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9F1))
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Mis Solicitudes de Adopción",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF244B57)
            )
        }

        when (state) {
            is SolicitudesState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF009688))
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

@Composable
fun SolicitudCard(solicitud: SolicitudResponse) {
    val estadoColor = when (solicitud.estadoSolicitud.nombre.uppercase()) {
        "APROBADA", "APROBADO" -> Color(0xFF66BB6A)
        "RECHAZADA", "RECHAZADO" -> Color(0xFFEF5350)
        "PENDIENTE" -> Color(0xFFFFA726)
        else -> Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = estadoColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = solicitud.estadoSolicitud.nombre.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = estadoColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Divider()

            Spacer(Modifier.height(12.dp))

            // Información adicional
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Store,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF757575)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = solicitud.animal.nombre,
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF757575)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Solicitado: ${formatDate(solicitud.fechaSolicitud)}",
                    fontSize = 14.sp,
                    color = Color(0xFF757575)
                )
            }

            // Mensaje del refugio si existe
            if (!solicitud.mensajeAlAdoptante.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💬 Mensaje del refugio:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF757575)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = solicitud.mensajeAlAdoptante,
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
