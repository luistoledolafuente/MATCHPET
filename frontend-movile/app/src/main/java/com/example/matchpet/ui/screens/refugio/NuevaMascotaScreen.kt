package com.example.matchpet.ui.screens.refugio

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.matchpet.data.model.LookupItem
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.AnimalFormState
import com.example.matchpet.viewmodel.NuevaMascotaViewModel
import com.example.matchpet.ui.theme.PrimaryTeal

// Definición de colores
val PrimaryTeal = Color(0xFF316B7A)
val SalmonAccent = Color(0xFFFDB2A0)

// --- Secciones del Formulario (Enum) ---
enum class PetFormSection(val title: String) {
    BASIC("Básico"),
    FEATURES("Características"),
    HEALTH("Salud"),
    PHOTOS("Fotos")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaMascotaScreen(
    onBack: () -> Unit,
    token: String,
    animalId: String?, // ID del animal si estamos editando, null si estamos creando
    viewModel: NuevaMascotaViewModel = viewModel(
        factory = Injection.provideNuevaMascotaViewModelFactory()
    )
) {
    val state by viewModel.state.collectAsState()
    val photoUrls by viewModel.photoUrls.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentSection by remember { mutableStateOf(PetFormSection.BASIC) }
    val scrollState = rememberScrollState()

    // Cargar catálogos inmediatamente
    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadLookups(token)
        }
    }

    // Lógica de Carga de Detalles para Edición
    LaunchedEffect(token, animalId) {
        if (!animalId.isNullOrEmpty() && token.isNotEmpty()) {
            viewModel.loadAnimalDetails(token, animalId)
        } else {
            viewModel.resetForm()
        }
    }

    // Manejo de Estados de la Operación (Éxito/Error)
    LaunchedEffect(state) {
        when (val s = state) {
            is AnimalFormState.Success -> {
                val message = if (s.isUpdate) "Mascota actualizada con éxito." else "Mascota registrada con éxito."
                snackbarHostState.showSnackbar(message, withDismissAction = true)
                viewModel.resetForm()
                onBack()
            }
            is AnimalFormState.Error -> {
                snackbarHostState.showSnackbar("Error: ${s.message}", withDismissAction = true)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    val isEditing = !animalId.isNullOrEmpty()
    val screenTitle = if (isEditing) "Editar Mascota" else "Nueva Mascota"
    val buttonText = if (isEditing) "Guardar Cambios" else "Registrar Mascota"
    val isLoading = state is AnimalFormState.LoadingDetails || state is AnimalFormState.Submitting || state is AnimalFormState.ImageUploading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(screenTitle, fontWeight = FontWeight.SemiBold, color = PrimaryTeal) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = PrimaryTeal)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8F6FA),
                    titleContentColor = PrimaryTeal
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.handleSubmit(token) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SalmonAccent)
            ) {
                if (state is AnimalFormState.Submitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(buttonText, style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F8FA))
        ) {
            SectionTabs(currentSection) { newSection ->
                currentSection = newSection
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state is AnimalFormState.LoadingDetails) {
                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                } else {
                    when (currentSection) {
                        PetFormSection.BASIC -> BasicInfoSection(viewModel)
                        PetFormSection.FEATURES -> FeaturesSection(viewModel)
                        PetFormSection.HEALTH -> HealthSection(viewModel)
                        // Pasamos el token a PhotosSection para la subida
                        PetFormSection.PHOTOS -> PhotosSection(viewModel, token, photoUrls)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 🔑 SECCIONES CONECTADAS AL VIEWMODEL
// -----------------------------------------------------------------------------

@Composable
fun BasicInfoSection(viewModel: NuevaMascotaViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Información Esencial", style = MaterialTheme.typography.titleMedium, color = PrimaryTeal)

        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        // Especie (Dropdown usando LookupItem)
        LookupDropdownField(
            label = "Especie",
            currentId = viewModel.especieId,
            options = viewModel.especies,
            onIdChange = { viewModel.especieId = it }
        )

        // Raza (Dropdown usando LookupItem)
        // Nota: Si Raza es un texto libre, usar OutlinedTextField. Si es catálogo, usar LookupDropdownField.
        // Asumo que Raza es un catálogo que se llena en el ViewModel después de seleccionar Especie.
        LookupDropdownField(
            label = "Raza",
            currentId = viewModel.razaId,
            options = viewModel.razas,
            onIdChange = { viewModel.razaId = it }
        )

        // Género (Dropdown usando LookupItem)
        LookupDropdownField(
            label = "Género",
            currentId = viewModel.generoId,
            options = viewModel.generos,
            onIdChange = { viewModel.generoId = it }
        )

        // Fecha de Nacimiento Aprox (String)
        OutlinedTextField(
            value = viewModel.fechaNacimientoAprox,
            onValueChange = { viewModel.fechaNacimientoAprox = it },
            label = { Text("Fecha Nacimiento Aprox (YYYY-MM-DD)") },
            placeholder = { Text("Ej: 2023-01-15") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun FeaturesSection(viewModel: NuevaMascotaViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Características Físicas y Comportamentales", style = MaterialTheme.typography.titleMedium, color = PrimaryTeal)

        // Tamaño (Dropdown usando LookupItem)
        LookupDropdownField(
            label = "Tamaño",
            currentId = viewModel.tamanoId,
            options = viewModel.tamanos,
            onIdChange = { viewModel.tamanoId = it }
        )

        // Nivel de Energía (Dropdown usando LookupItem)
        LookupDropdownField(
            label = "Nivel de Energía",
            currentId = viewModel.nivelEnergiaId,
            options = viewModel.nivelesEnergia,
            onIdChange = { viewModel.nivelEnergiaId = it }
        )

        // Descripción Personalidad (String - Multilínea)
        OutlinedTextField(
            value = viewModel.descripcionPersonalidad,
            onValueChange = { viewModel.descripcionPersonalidad = it },
            label = { Text("Descripción de la Personalidad") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        // Temperamentos (Implementación con Checkbox Chips)
        TemperamentosSection(viewModel = viewModel)
    }
}

@Composable
fun TemperamentosSection(viewModel: NuevaMascotaViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Temperamentos", style = MaterialTheme.typography.bodyLarge, color = PrimaryTeal)

        // Aquí usamos LazyRow para los chips de temperamento
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.temperamentosList) { item ->
                val isSelected = viewModel.temperamentoIds.contains(item.id)
                Chip(
                    label = item.nombre,
                    isSelected = isSelected,
                    onClick = { viewModel.toggleTemperamento(item.id, !isSelected) }
                )
            }
        }
    }
}


@Composable
fun HealthSection(viewModel: NuevaMascotaViewModel) {
    // ... (El contenido de HealthSection está correcto, solo necesita usar el nuevo LookupDropdownField si aplicara)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Estado de Salud", style = MaterialTheme.typography.titleMedium, color = PrimaryTeal)

        // Está Vacunado (Boolean)
        BooleanToggle(
            label = "Está Vacunado",
            isChecked = viewModel.estaVacunado,
            onCheckedChange = { viewModel.estaVacunado = it }
        )

        // Está Esterilizado (Boolean)
        BooleanToggle(
            label = "Está Esterilizado",
            isChecked = viewModel.estaEsterilizado,
            onCheckedChange = { viewModel.estaEsterilizado = it }
        )

        // Compatible con Niños (Boolean)
        BooleanToggle(
            label = "Compatible con Niños",
            isChecked = viewModel.compatibleNinos,
            onCheckedChange = { viewModel.compatibleNinos = it }
        )

        // Compatible con Otras Mascotas (Boolean)
        BooleanToggle(
            label = "Compatible con Otras Mascotas",
            isChecked = viewModel.compatibleOtrasMascotas,
            onCheckedChange = { viewModel.compatibleOtrasMascotas = it }
        )

        // Historial Médico (String - Multilínea)
        OutlinedTextField(
            value = viewModel.historialMedico,
            onValueChange = { viewModel.historialMedico = it },
            label = { Text("Historial Médico (Notas)") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        // Fecha Ingreso Refugio (String)
        OutlinedTextField(
            value = viewModel.fechaIngresoRefugio,
            onValueChange = { viewModel.fechaIngresoRefugio = it },
            label = { Text("Fecha Ingreso Refugio (YYYY-MM-DD)") },
            placeholder = { Text("Ej: 2024-05-20") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PhotosSection(viewModel: NuevaMascotaViewModel, token: String, photoUrls: List<String>) {
    val state by viewModel.state.collectAsState()
    val isUploading = state is AnimalFormState.ImageUploading

    // 🔑 Selector de imágenes de Android
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                // Iniciar la subida al ViewModel
                viewModel.uploadImage(token, it)
            }
        }
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Galería de Fotos (${photoUrls.size} subidas)", style = MaterialTheme.typography.titleMedium, color = PrimaryTeal)
        Text("Sube al menos una foto para que la mascota sea visible. Toca la foto para marcarla como principal.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Fotos subidas
            items(photoUrls.withIndex().toList()) { (index, url) ->
                val isPrincipal = viewModel.fotoPrincipalIndex == index
                PhotoItem(
                    url = url,
                    isPrincipal = isPrincipal,
                    onSetPrincipal = { viewModel.setPrincipalPhotoIndex(index) },
                    onRemove = { viewModel.removePhotoUrl(url) }
                )
            }

            // Botón para subir foto
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFDFF3FF))
                        .clickable(enabled = !isUploading) {
                            // Lanza el selector al hacer clic
                            imagePicker.launch("image/*")
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(Modifier.size(32.dp), color = PrimaryTeal)
                    } else {
                        Icon(
                            Icons.Default.AddAPhoto,
                            contentDescription = "Añadir foto",
                            tint = PrimaryTeal,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhotoItem(url: String, isPrincipal: Boolean, onSetPrincipal: () -> Unit, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSetPrincipal) // Marcar como principal al tocar
    ) {
        // Usar AsyncImage para cargar la URL real
        AsyncImage(
            model = url,
            contentDescription = "Foto de la mascota",
            modifier = Modifier.fillMaxSize()
        )

        // Indicador de principal
        if (isPrincipal) {
            Text(
                text = "PRINCIPAL",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(PrimaryTeal.copy(alpha = 0.7f))
                    .padding(vertical = 4.dp)
            )
        }

        // Botón de eliminar
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(50))
        ) {
            Icon(Icons.Default.Close, contentDescription = "Eliminar foto", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}


// -----------------------------------------------------------------------------
// 🔑 COMPONENTES REUTILIZABLES CORREGIDOS
// -----------------------------------------------------------------------------

@Composable
fun SectionTabs(current: PetFormSection, onSectionSelected: (PetFormSection) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(PetFormSection.entries.toTypedArray()) { section ->
            val isSelected = section == current
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) PrimaryTeal else Color(0xFFE8F6FA)
                )
            ) {
                Text(
                    text = section.title,
                    color = if (isSelected) Color.White else PrimaryTeal,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .clickable { onSectionSelected(section) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
    Divider(color = PrimaryTeal.copy(alpha = 0.1f))
}

// 🔑 NUEVO DropdownField que maneja IDs (Int?) y LookupItem
@Composable
fun LookupDropdownField(
    label: String,
    currentId: Int?,
    options: List<LookupItem>,
    onIdChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Buscar el nombre que corresponde al ID actual
    val currentName = options.find { it.id == currentId }?.nombre ?: "Seleccionar..."

    OutlinedTextField(
        value = currentName,
        onValueChange = { /* Solo cambia por el menú */ },
        label = { Text(label) },
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                Modifier.clickable { expanded = true }
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                text = { Text(option.nombre) },
                onClick = {
                    onIdChange(option.id) // Llama a la función que actualiza el ViewModel con el ID (Int)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun BooleanToggle(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.DarkGray)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SalmonAccent,
                checkedTrackColor = SalmonAccent.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun Chip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) PrimaryTeal else Color(0xFFE8F6FA))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else PrimaryTeal,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}