package com.example.matchpet.viewmodel.adoptante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: FavoritesRepository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Animal>>(emptyList())
    val favorites: StateFlow<List<Animal>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadFavorites(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getFavorites(token)
            result.onSuccess {
                _favorites.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun addFavorite(token: String, animalId: Int) {
        viewModelScope.launch {
            val result = repository.addFavorite(token, animalId)
            result.onSuccess {
                loadFavorites(token) // Reload to update list
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun removeFavorite(token: String, animalId: Int) {
        viewModelScope.launch {
            val result = repository.removeFavorite(token, animalId)
            result.onSuccess {
                loadFavorites(token) // Reload to update list
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    class Factory(private val repository: FavoritesRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoritesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
