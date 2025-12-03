package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.ui.theme.PaleTeal
import com.example.matchpet.ui.theme.WebBlueLight
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.adoptante.RecomendacionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendacionesScreen(
    token: String,
    navController: NavController,
    viewModel: RecomendacionesViewModel = viewModel(factory = Injection.provideRecomendacionesViewModelFactory())
) {
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val solicitudMsg by viewModel.solicitudMsg.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recomendaciones IA") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WebTeal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PaleTeal.copy(alpha = 0.25f),
                            WebBlueLight.copy(alpha = 0.25f)
                        )
                    )
                )
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Text(
                        text = error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                items.isEmpty() -> {
                    Text(
                        text = "No hay recomendaciones por ahora. Actualiza tu perfil.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    if (solicitudMsg != null) {
                        Snackbar(
                            modifier = Modifier.align(Alignment.TopCenter)
                        ) { Text(solicitudMsg ?: "") }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items.filter { it.estadoAdopcion.equals("Disponible", ignoreCase = true) }) { animal ->
                            RecomCard(
                                animal = animal,
                                onViewProfile = { navController.navigate("animal_detail/${animal.animal_id}") },
                                onSolicitar = { viewModel.solicitar(token, animal.animal_id) },
                                isFavorite = favoriteIds.contains(animal.animal_id),
                                onToggleFavorite = { viewModel.toggleFavorite(token, animal.animal_id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecomCard(
    animal: Animal,
    onViewProfile: () -> Unit,
    onSolicitar: () -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            ) {
                AsyncImage(
                    model = if (!animal.fotos.isNullOrEmpty()) "http://10.0.2.2:8081${animal.fotos[0]}" else "https://placehold.co/400x300/a8d8e0/316B7A?text=Match+IA",
                    contentDescription = animal.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0x99FFFFFF))
                ) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (isFavorite) Color(0xFFD32F2F) else Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = animal.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = WebTeal
                )
                Text(
                    text = "${animal.raza ?: "Mestizo"} • ${animal.genero ?: "?"}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    FilledIconButton(
                        onClick = onViewProfile,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = WebTeal,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Ver Perfil")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSolicitar,
                    enabled = animal.estadoAdopcion == "Disponible",
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WebTeal, contentColor = Color.White)
                ) {
                    Text("Solicitar")
                }
            }
        }
    }
}
