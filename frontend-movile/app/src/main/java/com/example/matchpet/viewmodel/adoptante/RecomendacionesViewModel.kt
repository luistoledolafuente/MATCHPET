package com.example.matchpet.viewmodel.adoptante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.data.repository.RecomendacionRepository
import com.example.matchpet.data.repository.SolicitudRepository
import com.example.matchpet.data.repository.FavoritesRepository
import com.example.matchpet.data.model.SolicitudRequest
import com.example.matchpet.utils.Injection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecomendacionesViewModel(
    private val repository: RecomendacionRepository,
    private val solicitudRepository: SolicitudRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Animal>>(emptyList())
    val items: StateFlow<List<Animal>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _solicitudMsg = MutableStateFlow<String?>(null)
    val solicitudMsg: StateFlow<String?> = _solicitudMsg.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    fun load(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getRecomendaciones(token)
            result.onSuccess { _items.value = it }
                .onFailure { _error.value = it.message }
            // cargar favoritos para mostrar el corazón
            try {
                val favRes = favoritesRepository.getFavorites(token)
                favRes.onSuccess { list -> _favoriteIds.value = list.map { it.animal_id }.toSet() }
                favRes.onFailure { /* mantener vacío si falla */ }
            } catch (_: Exception) {}
            _isLoading.value = false
        }
    }

    fun solicitar(token: String, animalId: Int, mensaje: String = "Estoy interesado en adoptar") {
        viewModelScope.launch {
            _solicitudMsg.value = null
            val res = solicitudRepository.createSolicitud(token, SolicitudRequest(animalId, mensaje))
            when (res) {
                is com.example.matchpet.data.repository.Resource.Success -> _solicitudMsg.value = "Solicitud enviada"
                is com.example.matchpet.data.repository.Resource.Error -> _solicitudMsg.value = res.message
                is com.example.matchpet.data.repository.Resource.Loading -> {}
            }
        }
    }

    fun toggleFavorite(token: String, animalId: Int) {
        viewModelScope.launch {
            val current = _favoriteIds.value
            val isFav = current.contains(animalId)
            val res = if (isFav) favoritesRepository.removeFavorite(token, animalId) else favoritesRepository.addFavorite(token, animalId)
            res.onSuccess {
                _favoriteIds.value = if (isFav) current - animalId else current + animalId
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    class Factory(
        private val repository: RecomendacionRepository,
        private val solicitudRepository: SolicitudRepository? = null,
        private val favoritesRepository: FavoritesRepository? = null
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RecomendacionesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RecomendacionesViewModel(
                    repository,
                    solicitudRepository ?: Injection.solicitudRepository,
                    favoritesRepository ?: Injection.favoritesRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
