package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.matchpet.data.model.AnimalResponse
import com.example.matchpet.viewmodel.MisMascotasViewModel
import com.example.matchpet.viewmodel.MisMascotasViewState
import com.example.matchpet.utils.Injection // Importar la clase de inyección

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisMascotasScreen(
    token: String,
    onNavigateToNewAnimal: () -> Unit, // Función para ir a la pantalla de añadir animal
    // ✅ Reemplazado el mock por la Factory real
    viewModel: MisMascotasViewModel = viewModel(
        factory = Injection.provideMisMascotasViewModelFactory()
    )
) {
    val viewState by viewModel.viewState.collectAsState()

    // Cargar datos al iniciar la pantalla
    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            // Llama al ViewModel para iniciar la carga
            viewModel.loadMisAnimales(token)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Mascotas", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F6FA),
                    titleContentColor = Color(0xFF316B7A)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewAnimal,
                containerColor = Color(0xFFFDB2A0),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Mascota")
            }
        },
        content = { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F8FA))
            ) {
                when (val state = viewState) {
                    is MisMascotasViewState.Initial -> {
                        // Si el estado es Inicial, muestra el indicador de carga
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFFDB2A0)
                        )
                        // Intenta cargar si el token ya llegó
                        LaunchedEffect(Unit) {
                            if (token.isNotEmpty()) {
                                viewModel.loadMisAnimales(token)
                            }
                        }
                    }
                    is MisMascotasViewState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFFDB2A0)
                        )
                    }
                    is MisMascotasViewState.Error -> {
                        Text(
                            text = state.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.Center).padding(24.dp)
                        )
                    }
                    is MisMascotasViewState.Success -> {
                        if (state.animales.isEmpty()) {
                            EmptyListMessage(modifier = Modifier.align(Alignment.Center))
                        } else {
                            AnimalList(state.animales)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AnimalList(animales: List<AnimalResponse>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(animales, key = { it.id }) { animal ->
            AnimalCard(animal)
        }
    }
}
@Composable
fun AnimalCard(animal: AnimalResponse) {

    val foto = animal.fotos?.firstOrNull()
    val fullImageUrl =
        if (foto != null && foto.startsWith("/")) {
            "${Injection.BACKEND_BASE_URL}$foto"
        } else {
            foto ?: "https://placehold.co/100x100?text=No+Foto"
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = rememberAsyncImagePainter(fullImageUrl),
                contentDescription = "Foto de ${animal.nombre}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = animal.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF316B7A),
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = "Raza",
                        Modifier.size(16.dp),
                        tint = Color(0xFFFDB2A0)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${animal.especie}, ${animal.raza}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Estado adopción (añadimos "Disponible")
            val statusColor = when (animal.estadoAdopcion) {
                "Aprobada" -> Color(0xFF28A745)
                "En Revisión" -> Color(0xFF007BFF)
                "Disponible" -> Color(0xFFFDBA34)
                else -> Color(0xFFDC3545)
            }

            Column(horizontalAlignment = Alignment.End) {

                Text(
                    text = animal.estadoAdopcion,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(Modifier.height(8.dp))

                // Edad no existe: mejor mostrar fecha de nacimiento aprox
                Text(
                    text = "Nacido: ${animal.fechaNacimientoAprox ?: "Sin dato"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}


@Composable
fun EmptyListMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Pets,
            contentDescription = "No hay mascotas",
            modifier = Modifier.size(64.dp).padding(8.dp),
            tint = Color(0xFFFDB2A0)
        )
        Text(
            text = "Aún no has registrado mascotas.",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF316B7A)
        )
        Text(
            text = "Toca el botón '+' para agregar un nuevo animal a la lista de adopción.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}