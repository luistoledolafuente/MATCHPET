package com.example.matchpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.AnimalResponse
import com.example.matchpet.data.repository.AnimalRepository
import com.example.matchpet.data.repository.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class MisMascotasViewState {
    object Initial : MisMascotasViewState()
    object Loading : MisMascotasViewState()
    data class Success(val animales: List<AnimalResponse>) : MisMascotasViewState()
    data class Error(val message: String) : MisMascotasViewState()
}

class MisMascotasViewModel(
    private val animalRepository: AnimalRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow<MisMascotasViewState>(MisMascotasViewState.Initial)
    val viewState: StateFlow<MisMascotasViewState> = _viewState.asStateFlow()

    fun loadMisAnimales(token: String) {
        _viewState.value = MisMascotasViewState.Loading
        viewModelScope.launch {
            animalRepository.getMisAnimales(token).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _viewState.value = MisMascotasViewState.Success(resource.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        val msg = resource.message ?: "Error desconocido al cargar mascotas."
                        Log.e("MisMascotasVM", msg)
                        _viewState.value = MisMascotasViewState.Error(msg)
                    }
                    is Resource.Loading -> {
                        // Mantener el estado de carga
                    }
                }
            }
        }
    }

    // 🔑 CLASE FACTORY - Anidada directamente
    class Factory(private val animalRepository: AnimalRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MisMascotasViewModel::class.java)) {
                return MisMascotasViewModel(animalRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}