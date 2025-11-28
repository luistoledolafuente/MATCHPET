package com.example.matchpet.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.AnimalCreationRequest
import com.example.matchpet.data.model.AnimalUpdateRequest
import com.example.matchpet.data.model.LookupItem
import com.example.matchpet.data.repository.AnimalRepository
import com.example.matchpet.data.repository.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnimalFormState {
    object Idle : AnimalFormState()
    object LoadingDetails : AnimalFormState()
    object LoadingLookups : AnimalFormState()
    object ImageUploading : AnimalFormState()
    data class ImageUploaded(val imageUrl: String) : AnimalFormState()
    object Submitting : AnimalFormState()
    data class Success(val isUpdate: Boolean) : AnimalFormState()
    data class Error(val message: String) : AnimalFormState()
}

class NuevaMascotaViewModel(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AnimalFormState>(AnimalFormState.Idle)
    val state: StateFlow<AnimalFormState> = _state.asStateFlow()

    private val _photoUrls = MutableStateFlow<List<String>>(emptyList())
    val photoUrls: StateFlow<List<String>> = _photoUrls.asStateFlow()

    var fotoPrincipalIndex by mutableStateOf(0)

    // --- VARIABLES DE ESTADO DEL FORMULARIO USANDO IDs ---
    var nombre by mutableStateOf("")
    var fechaNacimientoAprox by mutableStateOf("")
    var descripcionPersonalidad by mutableStateOf("")
    var historialMedico by mutableStateOf("")
    var fechaIngresoRefugio by mutableStateOf("")

    // IDs de catálogo
    var especieId by mutableStateOf<Int?>(null)
    var razaId by mutableStateOf<Int?>(null)
    var generoId by mutableStateOf<Int?>(null)
    var tamanoId by mutableStateOf<Int?>(null)
    var nivelEnergiaId by mutableStateOf<Int?>(null)
    var estadoAdopcionId by mutableStateOf<Int?>(null)

    // Booleanos
    var compatibleNinos by mutableStateOf(false)
    var compatibleOtrasMascotas by mutableStateOf(false)
    var estaVacunado by mutableStateOf(false)
    var estaEsterilizado by mutableStateOf(false)

    // Lista de IDs de temperamentos seleccionados
    private val _temperamentoIds = mutableStateOf<List<Int>>(emptyList())
    val temperamentoIds: List<Int> get() = _temperamentoIds.value

    // --- LOOKUPS (Catálogos para ComboBoxes en la UI) ---
    var especies by mutableStateOf<List<LookupItem>>(emptyList())
    var razas by mutableStateOf<List<LookupItem>>(emptyList())
    var generos by mutableStateOf<List<LookupItem>>(emptyList())
    var tamanos by mutableStateOf<List<LookupItem>>(emptyList())
    var nivelesEnergia by mutableStateOf<List<LookupItem>>(emptyList())
    var estadosAdopcion by mutableStateOf<List<LookupItem>>(emptyList())
    var temperamentosList by mutableStateOf<List<LookupItem>>(emptyList())


    private var animalIdToEdit: Int? = null

    // LÓGICA DE CARGA DE CATÁLOGOS
    fun loadLookups(token: String) = viewModelScope.launch {
        _state.value = AnimalFormState.LoadingLookups

        // Simulación de carga (Quitar cuando implementes tu Repositorio):
        if (generos.isEmpty()) {
            generos = listOf(LookupItem(1, "Macho"), LookupItem(2, "Hembra"))
            tamanos = listOf(LookupItem(1, "Pequeño"), LookupItem(2, "Mediano"), LookupItem(3, "Grande"))
            nivelesEnergia = listOf(LookupItem(1, "Bajo"), LookupItem(2, "Medio"), LookupItem(3, "Alto"))
            estadosAdopcion = listOf(LookupItem(1, "Disponible"), LookupItem(2, "Reservado"), LookupItem(3, "Adoptado"))

            // 🔑 DATOS DE PRUEBA AGREGADOS PARA SOLUCIONAR EL REPORTE DEL USUARIO
            especies = listOf(LookupItem(1, "Perro"), LookupItem(2, "Gato"), LookupItem(3, "Otro"))
            razas = listOf(
                LookupItem(1, "Mestizo"),
                LookupItem(2, "Labrador"),
                LookupItem(3, "Golden Retriever"),
                LookupItem(4, "Pastor Alemán"),
                LookupItem(5, "Siames"),
                LookupItem(6, "Persa"),
                LookupItem(7, "Bulldog")
            )
            temperamentosList = listOf(
                LookupItem(1, "Juguetón"),
                LookupItem(2, "Tímido"),
                LookupItem(3, "Cariñoso"),
                LookupItem(4, "Tranquilo"),
                LookupItem(5, "Protector"),
                LookupItem(6, "Energético")
            )
        }

        // Inicializar con valores por defecto
        if (generoId == null) generoId = generos.firstOrNull()?.id
        if (tamanoId == null) tamanoId = tamanos.find { it.nombre == "Mediano" }?.id
        if (nivelEnergiaId == null) nivelEnergiaId = nivelesEnergia.find { it.nombre == "Medio" }?.id
        if (estadoAdopcionId == null) estadoAdopcionId = estadosAdopcion.find { it.nombre == "Disponible" }?.id

        _state.value = AnimalFormState.Idle
    }

    // LÓGICA DE CARGA DE DATOS PARA EDICIÓN
    fun loadAnimalDetails(token: String, animalIdString: String?) {
        if (animalIdString.isNullOrEmpty()) return

        val id = animalIdString.toIntOrNull()
        if (id == null) {
            _state.value = AnimalFormState.Error("ID de animal inválido.")
            return
        }

        animalIdToEdit = id
        _state.value = AnimalFormState.LoadingDetails

        viewModelScope.launch {
            if (generos.isEmpty()) loadLookups(token)

            when (val result = animalRepository.getAnimalDetails(token, id)) {
                is Resource.Success -> {
                    result.data?.let { animal ->
                        nombre = animal.nombre
                        fechaNacimientoAprox = animal.fechaNacimientoAprox ?: ""
                        descripcionPersonalidad = animal.descripcionPersonalidad ?: ""
                        compatibleNinos = animal.compatibleNinos
                        compatibleOtrasMascotas = animal.compatibleOtrasMascotas
                        estaVacunado = animal.estaVacunado
                        estaEsterilizado = animal.estaEsterilizado
                        historialMedico = animal.historialMedico ?: ""
                        fechaIngresoRefugio = animal.fechaIngresoRefugio ?: ""

                        especieId = animal.especieId
                        razaId = animal.razaId
                        generoId = animal.generoId
                        tamanoId = animal.tamanoId
                        nivelEnergiaId = animal.nivelEnergiaId
                        estadoAdopcionId = animal.estadoAdopcionId

                        _photoUrls.value = animal.fotos ?: emptyList()
                        fotoPrincipalIndex = animal.fotoPrincipalIndex ?: 0
                        _temperamentoIds.value = animal.temperamentosIds.orEmpty()

                        _state.value = AnimalFormState.Idle
                    }
                }
                is Resource.Error -> {
                    _state.value = AnimalFormState.Error(result.message ?: "Error al cargar los detalles.")
                }
                else -> {}
            }
        }
    }

    // Función para manejar la selección de temperamentos (Checkbox)
    fun toggleTemperamento(temperamentoId: Int, isChecked: Boolean) {
        _temperamentoIds.value = if (isChecked) {
            _temperamentoIds.value + temperamentoId
        } else {
            _temperamentoIds.value.filter { it != temperamentoId }
        }
    }


    // LÓGICA DE ENVÍO (CREACIÓN O ACTUALIZACIÓN)
    fun handleSubmit(token: String) {
        if (nombre.isBlank() || razaId == null || especieId == null || generoId == null || tamanoId == null || nivelEnergiaId == null || estadoAdopcionId == null) {
            _state.value = AnimalFormState.Error("Faltan campos obligatorios de Nombre o de Catálogo (Raza, Especie, Género, Tamaño, Energía, Estado).")
            return
        }

        _state.value = AnimalFormState.Submitting
        viewModelScope.launch {
            if (animalIdToEdit != null) {
                updateAnimal(token)
            } else {
                registerAnimal(token)
            }
        }
    }

    // IMPLEMENTACIÓN DE CREACIÓN
    private suspend fun registerAnimal(token: String) {
        val request = AnimalCreationRequest(
            nombre = nombre,
            fechaNacimientoAprox = fechaNacimientoAprox.takeIf { it.isNotBlank() },
            descripcionPersonalidad = descripcionPersonalidad.takeIf { it.isNotBlank() },
            compatibleNinos = compatibleNinos,
            compatibleOtrasMascotas = compatibleOtrasMascotas,
            estaVacunado = estaVacunado,
            estaEsterilizado = estaEsterilizado,
            historialMedico = historialMedico.takeIf { it.isNotBlank() },
            fechaIngresoRefugio = fechaIngresoRefugio.takeIf { it.isNotBlank() },

            especieId = especieId!!,
            razaId = razaId!!,
            generoId = generoId!!,
            tamanoId = tamanoId!!,
            nivelEnergiaId = nivelEnergiaId!!,
            estadoAdopcionId = estadoAdopcionId!!,

            temperamentosIds = _temperamentoIds.value,
            fotosUrls = _photoUrls.value,
            fotoPrincipalIndex = fotoPrincipalIndex
        )
        when (val result = animalRepository.createAnimal(token, request)) {
            is Resource.Success -> {
                _state.value = AnimalFormState.Success(isUpdate = false)
            }
            is Resource.Error -> {
                _state.value = AnimalFormState.Error(result.message ?: "Fallo al crear mascota.")
            }
            else -> {}
        }
    }

    // IMPLEMENTACIÓN DE ACTUALIZACIÓN
    private suspend fun updateAnimal(token: String) {
        val id = animalIdToEdit!!
        val request = AnimalUpdateRequest(
            nombre = nombre.takeIf { it.isNotBlank() },
            fechaNacimientoAprox = fechaNacimientoAprox.takeIf { it.isNotBlank() },
            descripcionPersonalidad = descripcionPersonalidad.takeIf { it.isNotBlank() },
            compatibleNinos = compatibleNinos,
            compatibleOtrasMascotas = compatibleOtrasMascotas,
            estaVacunado = estaVacunado,
            estaEsterilizado = estaEsterilizado,
            historialMedico = historialMedico.takeIf { it.isNotBlank() },
            fechaIngresoRefugio = fechaIngresoRefugio.takeIf { it.isNotBlank() },

            especieId = this.especieId,
            razaId = this.razaId,
            generoId = this.generoId,
            tamanoId = this.tamanoId,
            nivelEnergiaId = this.nivelEnergiaId,
            estadoAdopcionId = this.estadoAdopcionId,

            temperamentosIds = _temperamentoIds.value,
            fotosUrls = _photoUrls.value,
            fotoPrincipalIndex = fotoPrincipalIndex
        )

        when (val result = animalRepository.updateAnimal(id, token, request)) {
            is Resource.Success -> {
                _state.value = AnimalFormState.Success(isUpdate = true)
            }
            is Resource.Error -> {
                _state.value = AnimalFormState.Error(result.message ?: "Fallo al actualizar mascota.")
            }
            else -> {}
        }
    }

    // 🔑 FUNCIÓN DE SUBIDA DE IMAGEN (uploadImage)
    fun uploadImage(token: String, fileUri: Uri) = viewModelScope.launch {
        _state.value = AnimalFormState.ImageUploading

        // La función animalRepository.uploadImage debe existir y devolver la URL en ImageUploadResponse
        when (val result = animalRepository.uploadImage(token, fileUri)) {
            is Resource.Success -> {
                val newUrl = result.data?.url ?: ""
                if (newUrl.isNotBlank()) {
                    // 1. Añade la URL a la lista de fotos
                    _photoUrls.value = _photoUrls.value + newUrl
                    // 2. Establece el estado de éxito con la nueva URL
                    _state.value = AnimalFormState.ImageUploaded(newUrl)
                } else {
                    _state.value = AnimalFormState.Error("La subida fue exitosa, pero no se recibió la URL.")
                }
            }
            is Resource.Error -> {
                _state.value = AnimalFormState.Error(result.message ?: "Fallo al subir la imagen.")
            }
            else -> {}
        }
    }

    // 🔑 FUNCIONES AUXILIARES PARA MANEJAR LA LISTA DE FOTOS
    fun removePhotoUrl(url: String) {
        val oldUrls = _photoUrls.value
        val newUrls = oldUrls.filter { it != url }
        _photoUrls.value = newUrls

        // Si se elimina la foto principal, resetear el índice a 0 o al último elemento
        if (fotoPrincipalIndex >= newUrls.size) {
            fotoPrincipalIndex = if (newUrls.isEmpty()) 0 else newUrls.size - 1
        }
    }

    fun setPrincipalPhotoIndex(index: Int) {
        if (index in _photoUrls.value.indices) {
            fotoPrincipalIndex = index
        }
    }

    fun resetState() {
        _state.value = AnimalFormState.Idle
    }

    fun resetForm() {
        nombre = ""
        fechaNacimientoAprox = ""
        descripcionPersonalidad = ""
        historialMedico = ""
        fechaIngresoRefugio = ""

        especieId = null
        razaId = null
        generoId = generos.firstOrNull()?.id
        tamanoId = tamanos.find { it.nombre == "Mediano" }?.id
        nivelEnergiaId = nivelesEnergia.find { it.nombre == "Medio" }?.id
        estadoAdopcionId = estadosAdopcion.find { it.nombre == "Disponible" }?.id

        compatibleNinos = false
        compatibleOtrasMascotas = false
        estaVacunado = false
        estaEsterilizado = false

        animalIdToEdit = null
        _photoUrls.value = emptyList()
        fotoPrincipalIndex = 0
        _temperamentoIds.value = emptyList()
        resetState()
    }

    class Factory(private val animalRepository: AnimalRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NuevaMascotaViewModel::class.java)) {
                return NuevaMascotaViewModel(animalRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}