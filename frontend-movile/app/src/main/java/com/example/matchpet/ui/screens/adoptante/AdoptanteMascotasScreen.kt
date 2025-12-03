package com.example.matchpet.ui.screens.adoptante

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pets
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
import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.data.model.SolicitudRequest
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.ui.theme.PaleTeal
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.adoptante.FavoritesViewModel
import kotlinx.coroutines.launch

@Composable
fun AdoptanteMascotasScreen(
    token: String,
    paddingValues: PaddingValues,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel = viewModel(factory = Injection.provideFavoritesViewModelFactory())
) {
    var mascotas by remember { mutableStateOf<List<Animal>>(emptyList()) }
    var misSolicitudes by remember { mutableStateOf<List<com.example.matchpet.data.model.SolicitudResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Estados para el Modal de Solicitud
    var showDialog by remember { mutableStateOf(false) }
    var selectedAnimal by remember { mutableStateOf<Animal?>(null) }
    var mensajeAdoptante by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }

    // Estado de favoritos
    val favorites by favoritesViewModel.favorites.collectAsState()
    val favoritesError by favoritesViewModel.error.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Debugging Toasts
    LaunchedEffect(favoritesError) {
        favoritesError?.let {
            Toast.makeText(context, "Error Favoritos: $it", Toast.LENGTH_LONG).show()
        }
    }

    // Cargar Mascotas, Favoritos y Solicitudes
    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites(token)
        try {
            // Cargar mascotas
            val response = RetrofitClient.api.getAnimales()
            if (response.isSuccessful) {
                val allMascotas = response.body()?.content ?: emptyList()
                mascotas = allMascotas.filter { 
                    !it.estadoAdopcion.equals("Adoptado", ignoreCase = true)
                }
            } else {
                error = "Error al cargar mascotas: ${response.message()}"
            }
            
            // Cargar mis solicitudes
            val solicitudesResp = RetrofitClient.api.getMisSolicitudes("Bearer $token")
            if (solicitudesResp.isSuccessful) {
                misSolicitudes = solicitudesResp.body() ?: emptyList()
            }

        } catch (e: Exception) {
            error = "Error de conexión: ${e.message}"
        } finally {
            loading = false
        }
    }

    // UI Principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaleTeal)
            .padding(paddingValues)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = WebTeal
            )
        } else if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Explorar Mascotas 🐾",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004D40),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (mascotas.isEmpty()) {
                    Text("No hay mascotas disponibles por ahora.", color = Color.Gray)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(mascotas) { animal ->
                            val isFavorite = favorites.any { it.animal_id == animal.animal_id }
                            val isRequested = misSolicitudes.any { it.animal.id == animal.animal_id }
                            
                            MascotaGridItem(
                                animal = animal,
                                isFavorite = isFavorite,
                                isRequested = isRequested,
                                onToggleFavorite = {
                                    if (isFavorite) {
                                        favoritesViewModel.removeFavorite(token, animal.animal_id)
                                    } else {
                                        favoritesViewModel.addFavorite(token, animal.animal_id)
                                    }
                                },
                                onSolicitarClick = {
                                    selectedAnimal = animal
                                    showDialog = true
                                },
                                onItemClick = {
                                    navController.navigate("animal_detail/${animal.animal_id}")
                                }
                            )
                        }
                    }
                }
            }
        }

        // Modal de Solicitud
        if (showDialog && selectedAnimal != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Adoptar a ${selectedAnimal!!.nombre}", color = WebTeal) },
                text = {
                    Column {
                        Text("Escribe un mensaje al refugio explicando por qué eres el adoptante ideal:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = mensajeAdoptante,
                            onValueChange = { mensajeAdoptante = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WebTeal,
                                cursorColor = WebTeal
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (mensajeAdoptante.isBlank()) {
                                Toast.makeText(context, "El mensaje es obligatorio", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            scope.launch {
                                submitting = true
                                try {
                                    val request = SolicitudRequest(
                                        animalId = selectedAnimal!!.animal_id,
                                        mensajeAdoptante = mensajeAdoptante
                                    )
                                    val response = RetrofitClient.api.createSolicitud("Bearer $token", request)
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Solicitud enviada con éxito", Toast.LENGTH_LONG).show()
                                        
                                        // Actualizar estado de la mascota a "En proceso" (ID 2)
                                        try {
                                            RetrofitClient.api.updateAnimal(
                                                id = selectedAnimal!!.animal_id,
                                                token = "Bearer $token",
                                                request = com.example.matchpet.data.model.animal.AnimalUpdateRequest(estadoAdopcionId = 2)
                                            )
                                        } catch (_: Exception) {}

                                        showDialog = false
                                        mensajeAdoptante = ""
                                        // Recargar solicitudes para actualizar UI
                                        val solicitudesResp = RetrofitClient.api.getMisSolicitudes("Bearer $token")
                                        if (solicitudesResp.isSuccessful) {
                                            misSolicitudes = solicitudesResp.body() ?: emptyList()
                                        }
                                    } else {
                                        Toast.makeText(context, "Error al enviar: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    submitting = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                        enabled = !submitting
                    ) {
                        if (submitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Enviar Solicitud")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun MascotaGridItem(
    animal: Animal,
    isFavorite: Boolean,
    isRequested: Boolean,
    onToggleFavorite: () -> Unit,
    onSolicitarClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
    ) {
        Box {
            Column {
                // Imagen
                AsyncImage(
                    model = if (!animal.fotos.isNullOrEmpty()) "http://10.0.2.2:8081${animal.fotos[0]}" else "https://placehold.co/400x300/B2D8D8/004D40?text=No+Photo",
                    contentDescription = animal.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = animal.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF004D40)
                    )
                    Text(
                        text = "${animal.raza ?: "Mestizo"} • ${animal.genero ?: "?"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = animal.refugioNombre ?: "Refugio",
                        fontSize = 12.sp,
                        color = WebTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = onSolicitarClick,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRequested) Color.Gray else WebTeal
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        enabled = !isRequested
                    ) {
                        Text(if (isRequested) "Solicitado" else "Solicitar", fontSize = 12.sp)
                    }
                }
            }

            // Botón de Favorito (Superpuesto)
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
