package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.activity.compose.BackHandler
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.matchpet.data.model.animal.AnimalResponse
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.adoptante.FavoritesViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.matchpet.data.model.SolicitudRequest
import kotlinx.coroutines.launch

// Función para calcular la edad desde la fecha de nacimiento
// (Mantengo la función existente)
fun calcularEdad(fechaNacimiento: String?): String {
    if (fechaNacimiento.isNullOrBlank()) return "Desconocida"
    return try {
        val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = java.time.LocalDate.parse(fechaNacimiento, formatter)
        val currentDate = java.time.LocalDate.now()
        val period = java.time.Period.between(birthDate, currentDate)
        val years = period.years
        val months = period.months
        when {
            years > 0 -> "$years año${if (years > 1) "s" else ""}"
            months > 0 -> "$months mes${if (months > 1) "es" else ""}"
            else -> "Menos de 1 mes"
        }
    } catch (e: Exception) {
        "Desconocida"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    animalId: Int,
    token: String,
    navController: NavController,
    favoritesViewModel: FavoritesViewModel = viewModel(factory = Injection.provideFavoritesViewModelFactory())
) {
    var animal by remember { mutableStateOf<AnimalResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedImageIndex by remember { mutableStateOf(0) }
    var isRequested by remember { mutableStateOf(false) }

    // Estados para el Modal de Solicitud
    var showDialog by remember { mutableStateOf(false) }
    var mensajeAdoptante by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Estado de favoritos
    val favorites by favoritesViewModel.favorites.collectAsState()
    val favoritesError by favoritesViewModel.error.collectAsState()
    val isFavorite = favorites.any { it.animal_id == animalId }

    // Debugging Toasts for Favorites
    LaunchedEffect(favoritesError) {
        favoritesError?.let {
            Toast.makeText(context, "Error Favoritos: $it", Toast.LENGTH_LONG).show()
        }
    }

    // Cargar favoritos al inicio
    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites(token)
    }
    
    // Cargar datos del animal
    LaunchedEffect(animalId) {
        try {
            val response = RetrofitClient.api.getAnimalDetails(animalId, "Bearer $token")
            if (response.isSuccessful) {
                animal = response.body()
                
                // Verificar si ya existe una solicitud
                val requestsResponse = RetrofitClient.api.getMisSolicitudes("Bearer $token")
                if (requestsResponse.isSuccessful) {
                    val requests = requestsResponse.body() ?: emptyList()
                    isRequested = requests.any { it.animal.id == animalId }
                }
            } else {
                error = "Error al cargar la mascota"
            }
        } catch (e: Exception) {
            error = "Error de conexión: ${e.message}"
        } finally {
            loading = false
        }
    }
    
    BackHandler { navController.popBackStack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de ${animal?.nombre ?: "Mascota"}", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WebTeal),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color(0xFFFFF7E6)
    ) { paddingValues ->
        
        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = WebTeal)
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error!!, color = Color.Red)
                }
            }
            animal != null -> {
                AnimalDetailContent(
                    animal = animal!!,
                    selectedImageIndex = selectedImageIndex,
                    onImageSelected = { selectedImageIndex = it },
                    modifier = Modifier.padding(paddingValues),
                    navController = navController,
                    isFavorite = isFavorite,
                    onToggleFavorite = {
                        if (isFavorite) {
                            favoritesViewModel.removeFavorite(token, animalId)
                        } else {
                            favoritesViewModel.addFavorite(token, animalId)
                        }
                    },
                    onRequestAdoption = { showDialog = true },
                    isRequested = isRequested
                )

                // Modal de Solicitud
                if (showDialog && animal != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Adoptar a ${animal!!.nombre}", color = WebTeal) },
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
                                                animalId = animal!!.id,
                                                mensajeAdoptante = mensajeAdoptante
                                            )
                                            val response = RetrofitClient.api.createSolicitud("Bearer $token", request)
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Solicitud enviada con éxito", Toast.LENGTH_LONG).show()
                                                
                                                // Actualizar estado de la mascota a "En proceso" (ID 2)
                                                try {
                                                    RetrofitClient.api.updateAnimal(
                                                        id = animal!!.id,
                                                        token = "Bearer $token",
                                                        request = com.example.matchpet.data.model.animal.AnimalUpdateRequest(estadoAdopcionId = 2)
                                                    )
                                                } catch (_: Exception) {}

                                                showDialog = false
                                                mensajeAdoptante = ""
                                                isRequested = true // Actualizar estado localmente
                                                // navController.popBackStack() // Opcional: Mantener en pantalla para ver cambio
                                                Toast.makeText(context, "Solicitud enviada. Botón deshabilitado.", Toast.LENGTH_SHORT).show()
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
    }
}

@Composable
fun AnimalDetailContent(
    animal: AnimalResponse,
    selectedImageIndex: Int,
    onImageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onRequestAdoption: () -> Unit,
    isRequested: Boolean
) {
    val scrollState = rememberScrollState()
    val imagenes = if (!animal.fotos.isNullOrEmpty()) {
        animal.fotos.map { "http://10.0.2.2:8081$it" }
    } else {
        listOf("https://placehold.co/600x400/e2e8f0/cbd5e1?text=Sin+Foto")
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Galería de fotos
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Imagen principal
                AsyncImage(
                    model = imagenes[selectedImageIndex],
                    contentDescription = animal.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Miniaturas
                if (imagenes.size > 1) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        imagenes.forEachIndexed { index, url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .then(
                                        if (index == selectedImageIndex) 
                                            Modifier.background(WebTeal.copy(alpha = 0.3f))
                                        else Modifier
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
        
        // Información principal
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Nombre y estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = animal.nombre,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = WebTeal
                    )
                    
                    Surface(
                        color = if (animal.estadoAdopcion == "Disponible") Color(0xFF4CAF50) else Color(0xFFFF9800),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = animal.estadoAdopcion ?: "Desconocido",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                // Refugio
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = animal.refugioNombre ?: "Refugio",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(Modifier.height(20.dp))
                
                // Datos clave en grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoCard("Raza", animal.raza ?: "Mestizo", Color(0xFFE3F2FD), Color(0xFF2196F3), Modifier.weight(1f))
                    InfoCard("Edad", calcularEdad(animal.fechaNacimientoAprox), Color(0xFFF3E5F5), Color(0xFF9C27B0), Modifier.weight(1f))
                }
                
                Spacer(Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoCard("Género", animal.genero ?: "?", Color(0xFFFCE4EC), Color(0xFFE91E63), Modifier.weight(1f))
                    InfoCard("Tamaño", animal.tamano ?: "?", Color(0xFFFFF9C4), Color(0xFFFDD835), Modifier.weight(1f))
                }
            }
        }
        
        // Sobre la mascota
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Sobre ${animal.nombre}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WebTeal
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = animal.descripcionPersonalidad ?: "Sin descripción disponible.",
                    fontSize = 14.sp,
                    color = Color(0xFF616161),
                    lineHeight = 22.sp
                )
            }
        }
        
        // Características
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Características",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WebTeal
                )
                Spacer(Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (animal.estaVacunado == true) {
                        CharacteristicChip("💉 Vacunado", Color(0xFFB2DFDB), Color(0xFF00897B))
                    }
                    if (animal.estaEsterilizado == true) {
                        CharacteristicChip("🩺 Esterilizado", Color(0xFFC5CAE9), Color(0xFF3F51B5))
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (animal.compatibleNinos == true) {
                        CharacteristicChip("👶 Compatible con niños", Color(0xFFFFF9C4), Color(0xFFF57C00))
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                
                if (animal.compatibleOtrasMascotas == true) {
                    CharacteristicChip("🐶 Amigable con mascotas", Color(0xFFFFCCBC), Color(0xFFE64A19))
                }
            }
        }
        
        // Botones de acción (Solicitar y Favorito)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Botón de Favorito
            OutlinedButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .weight(0.3f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isFavorite) Color.Red else Color.Gray
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isFavorite) Color.Red else Color.Gray)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorito",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Botón de Adopción
            Button(
                onClick = onRequestAdoption,
                modifier = Modifier
                    .weight(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRequested) Color.Gray else WebTeal
                ),
                enabled = !isRequested
            ) {
                Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isRequested) "Solicitud Enviada" else "¡Quiero Adoptarlo!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
fun InfoCard(label: String, value: String, backgroundColor: Color, textColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF424242)
            )
        }
    }
}

@Composable
fun CharacteristicChip(text: String, backgroundColor: Color, textColor: Color) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}
