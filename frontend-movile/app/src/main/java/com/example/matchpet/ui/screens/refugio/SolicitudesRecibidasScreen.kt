package com.example.matchpet.ui.screens.refugio

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.matchpet.data.model.animal.LookupItem
import com.example.matchpet.data.model.SolicitudResponse
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.data.repository.SolicitudRepository
import com.example.matchpet.viewmodel.SolicitudesRecibidasState
import com.example.matchpet.viewmodel.SolicitudesRecibidasViewModel

val WebTeal = Color(0xFF008080)
val PaleTeal = Color(0xFFB2D8D8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesRecibidasScreen(token: String) {

    val solicitudRepository = remember { SolicitudRepository(RetrofitClient.api) }
    val viewModel: SolicitudesRecibidasViewModel = viewModel(
        factory = SolicitudesRecibidasViewModel.Factory(solicitudRepository)
    )

    val state by viewModel.state.collectAsState()
    val solicitudes by viewModel.solicitudes.collectAsState()
    val estadosDisponibles by viewModel.estadosDisponibles

    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.loadSolicitudes(token) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes de Adopción Recibidas", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WebTeal),
                actions = {
                    IconButton(onClick = { viewModel.loadSolicitudes(token) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar", tint = Color.White)
                    }
                }
            )
        },
        containerColor = PaleTeal
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {

                is SolicitudesRecibidasState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = WebTeal)
                    }
                }

                is SolicitudesRecibidasState.Error -> {
                    val msg = (state as SolicitudesRecibidasState.Error).message

                    LaunchedEffect(msg) {
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error al cargar datos. Toca para reintentar.",
                            color = Color.Red,
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable { viewModel.loadSolicitudes(token) }
                        )
                    }
                }

                else -> {
                    if (solicitudes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay solicitudes de adopción recibidas.")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = solicitudes,
                                key = { it.id }
                            ) { solicitud ->
                                SolicitudCard(
                                    solicitud = solicitud,
                                    estadosDisponibles = estadosDisponibles,
                                    onStatusChange = { nuevoEstadoId ->
                                        viewModel.updateSolicitudStatus(
                                            token,
                                            solicitud.id,
                                            nuevoEstadoId
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SolicitudCard(
    solicitud: SolicitudResponse,
    estadosDisponibles: List<LookupItem>,
    onStatusChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    val estadoNombre = solicitud.estadoSolicitud.nombre
    val adoptanteNombre = solicitud.adoptante.nombreCompleto
    val animalNombre = solicitud.animal.nombre

    val foto =
        solicitud.animal.fotos?.firstOrNull()
            ?: "https://placehold.co/100x100/CCCCCC/000000?text=SIN+FOTO"

    val mensaje = solicitud.mensajeAlAdoptante ?: "No hay mensaje."
    val fecha = solicitud.fechaSolicitud
    val email = solicitud.adoptante.email
    val telefono = solicitud.adoptante.telefono ?: "N/D"

    val isPending =
        estadoNombre.equals("Pendiente", true) || estadoNombre.equals("Enviada", true)

    val cardColor = when (estadoNombre) {
        "Aprobada" -> Color(0xFFC8E6C9)
        "Rechazada" -> Color(0xFFFFCDD2)
        else -> Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = rememberAsyncImagePainter(foto),
                    contentDescription = "Foto mascota",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Text("Mascota: $animalNombre", fontWeight = FontWeight.Bold)
                    Text("Adoptante: $adoptanteNombre")
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = estadoNombre,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPending) WebTeal else Color.Black
                    )
                    Icon(
                        Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }
            }

            if (expanded) {

                Divider(Modifier.padding(vertical = 8.dp))

                Text("Fecha: $fecha")
                Text("Email: $email")
                Text("Teléfono: $telefono")

                Spacer(Modifier.height(8.dp))

                Text("Mensaje del Adoptante:", fontWeight = FontWeight.SemiBold)
                Text(
                    text = mensaje,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PaleTeal.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )

                Spacer(Modifier.height(12.dp))

                Box {
                    OutlinedButton(onClick = { showDropdown = true }) {
                        Text("Cambiar Estado")
                        Icon(Icons.Filled.ExpandMore, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = showDropdown,
                        onDismissRequest = { showDropdown = false }
                    ) {
                        estadosDisponibles.forEach { estado ->
                            DropdownMenuItem(
                                text = { Text(estado.nombre) },
                                onClick = {
                                    onStatusChange(estado.id)
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
