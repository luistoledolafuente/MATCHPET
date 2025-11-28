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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.matchpet.ui.theme.PrimaryTeal
import com.example.matchpet.viewmodel.DeleteAnimalState // 🔑 Importado
import com.example.matchpet.viewmodel.MisMascotasViewModel
import com.example.matchpet.viewmodel.MisMascotasViewState
import com.example.matchpet.utils.Injection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisMascotasScreen(
    token: String,
    onNavigateToNewAnimal: () -> Unit,
    // 🔑 CORRECCIÓN DE TIPO: Esperamos Int (el ID)
    onNavigateToEditAnimal: (Int) -> Unit,
    viewModel: MisMascotasViewModel = viewModel(
        factory = Injection.provideMisMascotasViewModelFactory()
    )
) {
    val viewState by viewModel.viewState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    // 🔑 Estados para el modal de confirmación de eliminación
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var animalToDelete by remember { mutableStateOf<AnimalResponse?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // ... [LaunchedEffect para cargar datos (existente)] ...
    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadMisAnimales(token)
        }
    }

    // 🔑 Manejar el estado de eliminación (Mostrar SnackBar)
    LaunchedEffect(deleteState) {
        when (val state = deleteState) {
            is DeleteAnimalState.Deleted -> {
                snackbarHostState.showSnackbar("Mascota eliminada exitosamente.", withDismissAction = true)
                viewModel.resetDeleteState()
            }
            is DeleteAnimalState.Error -> {
                snackbarHostState.showSnackbar("Error al eliminar: ${state.message}", withDismissAction = true)
                viewModel.resetDeleteState()
            }
            else -> {}
        }
    }

    // 🔑 Función para iniciar el borrado
    val startDeleteProcess: (AnimalResponse) -> Unit = { animal ->
        animalToDelete = animal
        showDeleteConfirmation = true
    }

    val confirmDelete: () -> Unit = {
        // 🔑 USO CORRECTO: animalToDelete?.id es Int? y se lo pasamos al ViewModel
        animalToDelete?.id?.let { id ->
            viewModel.deleteAnimal(id)
        }
        showDeleteConfirmation = false
        animalToDelete = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Mascotas", fontWeight = FontWeight.SemiBold, color = PrimaryTeal) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },

        // ✅ BOTÓN DE AGREGAR: FloatingActionButton en su sitio
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNewAnimal,
                containerColor = Color(0xFFFDB2A0) // Color para evitar el error de AccentOrange
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Mascota", tint = Color.White)
            }
        },
        content = { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F8FA))
            ) {
                // ... [Manejo de estados Initial, Loading, Error (existente)] ...
                when (val state = viewState) {
                    is MisMascotasViewState.Initial, is MisMascotasViewState.Loading -> {
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
                            AnimalList(
                                animales = state.animales,
                                onEdit = onNavigateToEditAnimal, // 🔑 Pasando función de edición
                                onDelete = startDeleteProcess    // 🔑 Pasando función de borrado
                            )
                        }
                    }
                }

                // 🔑 Modal de Confirmación de Eliminación
                if (showDeleteConfirmation && animalToDelete != null) {
                    DeleteConfirmationDialog(
                        animalName = animalToDelete!!.nombre,
                        onConfirm = confirmDelete,
                        onDismiss = {
                            showDeleteConfirmation = false
                            animalToDelete = null
                        },
                        isDeleting = deleteState is DeleteAnimalState.Deleting
                    )
                }
            }
        }
    )
}

@Composable
fun AnimalList(
    animales: List<AnimalResponse>,
    // 🔑 CORRECCIÓN: onEdit espera Int
    onEdit: (Int) -> Unit,
    onDelete: (AnimalResponse) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(animales, key = { it.id }) { animal ->
            AnimalCard(animal, onEdit, onDelete)
        }
    }
}

@Composable
fun AnimalCard(
    animal: AnimalResponse,
    // 🔑 CORRECCIÓN: onEdit espera Int
    onEdit: (Int) -> Unit,
    onDelete: (AnimalResponse) -> Unit
) {
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
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // --- Contenido de la Mascota (Fila) (existente) ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ... [Image, Nombre, Raza, Estado Adopción, Fecha Nacimiento (existente)] ...
            Image(
                painter = rememberAsyncImagePainter(
                    if (animal.fotos?.firstOrNull() != null && animal.fotos.first().startsWith("/")) {
                        "${Injection.BACKEND_BASE_URL}${animal.fotos.first()}"
                    } else {
                        animal.fotos?.firstOrNull() ?: "https://placehold.co/100x100?text=No+Foto"
                    }
                ),
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
                Text(
                    text = "Nacido: ${animal.fechaNacimientoAprox ?: "Sin dato"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }

        // --- 🔑 Botones de CRUD (Editar y Eliminar) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8F6FA))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onEdit(animal.id) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF316B7A) // Azul oscuro para Editar
                ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("Editar", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { onDelete(animal) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDB2A0) // Salmón suave para Eliminar
                ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("Eliminar", fontWeight = FontWeight.SemiBold)
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


// 🔑 NUEVO: Dialogo de Confirmación para Eliminar
@Composable
fun DeleteConfirmationDialog(
    animalName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDeleting: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Eliminación", color = Color(0xFFDC3545), fontWeight = FontWeight.Bold) },
        text = {
            Text("¿Estás seguro de que deseas eliminar a '$animalName'? Esta acción es irreversible y eliminará su ficha de adopción.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Eliminar")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF316B7A))
            }
        }
    )
}
