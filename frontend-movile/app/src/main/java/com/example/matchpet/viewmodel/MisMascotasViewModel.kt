package com.example.matchpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.animal.AnimalResponse
import com.example.matchpet.data.repository.AnimalRepository
import com.example.matchpet.data.repository.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MisMascotasViewState {
    object Initial : MisMascotasViewState()
    object Loading : MisMascotasViewState()
    data class Success(val animales: List<AnimalResponse>) : MisMascotasViewState()
    data class Error(val message: String) : MisMascotasViewState()
}

sealed class DeleteAnimalState {
    object Idle : DeleteAnimalState()
    object Deleting : DeleteAnimalState()
    data class Deleted(val animalId: Int) : DeleteAnimalState()
    data class Error(val message: String) : DeleteAnimalState()
}


class MisMascotasViewModel(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<MisMascotasViewState>(MisMascotasViewState.Initial)
    val viewState: StateFlow<MisMascotasViewState> = _viewState.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteAnimalState>(DeleteAnimalState.Idle)
    val deleteState: StateFlow<DeleteAnimalState> = _deleteState.asStateFlow()

    private var currentToken: String = ""

    /**
     * Carga la lista de animales. Asume que animalRepository.getMisAnimales(token) devuelve Flow<Resource<...>>
     */
    fun loadMisAnimales(token: String) {
        currentToken = token
        _viewState.value = MisMascotasViewState.Loading

        viewModelScope.launch {
            animalRepository.getMisAnimales(token).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _viewState.value = MisMascotasViewState.Success(resource.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        // ✅ CORRECCIÓN DE SINTAXIS: Se accede a la propiedad 'message' de la variable 'resource'
                        _viewState.value = MisMascotasViewState.Error(resource.message ?: "Error desconocido al cargar.")
                    }
                    is Resource.Loading -> {
                        // Mantener el estado de carga
                    }
                }
            }
        }
    }

    /**
     * Elimina una mascota (manteniendo el formato de suspend function de la versión anterior)
     */
    fun deleteAnimal(animalId: Int) {
        if (_deleteState.value is DeleteAnimalState.Deleting) return

        if (currentToken.isEmpty()) {
            _deleteState.value = DeleteAnimalState.Error("Token no disponible. Recarga la pantalla.")
            return
        }

        _deleteState.value = DeleteAnimalState.Deleting
        viewModelScope.launch {
            // El formato 'when (val result = ...)' es correcto para suspend functions
            when (val result = animalRepository.deleteAnimal(currentToken, animalId.toString())) {
                is Resource.Success -> {
                    val currentList = (_viewState.value as? MisMascotasViewState.Success)?.animales ?: emptyList()
                    val updatedList = currentList.filter { it.id != animalId }
                    _viewState.value = MisMascotasViewState.Success(updatedList)
                    _deleteState.value = DeleteAnimalState.Deleted(animalId)
                }
                is Resource.Error -> {
                    _deleteState.value = DeleteAnimalState.Error(result.message ?: "Error desconocido en el servidor.")
                }
                is Resource.Loading -> { /* Not needed here for a suspend fun */ }
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteAnimalState.Idle
    }

    class Factory(
        private val animalRepository: AnimalRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MisMascotasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MisMascotasViewModel(animalRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}