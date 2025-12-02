package com.example.matchpet.viewmodel.adoptante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.SolicitudResponse
import com.example.matchpet.data.repository.Resource
import com.example.matchpet.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class SolicitudesState {
    object Idle : SolicitudesState()
    object Loading : SolicitudesState()
    data class Success(val solicitudes: List<SolicitudResponse>) : SolicitudesState()
    data class Error(val message: String) : SolicitudesState()
}

class AdoptanteSolicitudesViewModel(
    private val solicitudRepository: SolicitudRepository
) : ViewModel() {

    private val _solicitudesState = MutableStateFlow<SolicitudesState>(SolicitudesState.Idle)
    val solicitudesState: StateFlow<SolicitudesState> = _solicitudesState.asStateFlow()

    fun loadSolicitudes(token: String) {
        _solicitudesState.value = SolicitudesState.Loading
        viewModelScope.launch {
            try {
                when (val result = solicitudRepository.getMisSolicitudes(token)) {
                    is Resource.Success -> {
                        _solicitudesState.value = SolicitudesState.Success(result.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _solicitudesState.value = SolicitudesState.Error(result.message ?: "Error desconocido")
                    }
                    is Resource.Loading -> {
                        // Already in loading state
                    }
                }
            } catch (e: Exception) {
                Log.e("SolicitudesVM", "Error loading solicitudes", e)
                _solicitudesState.value = SolicitudesState.Error(e.message ?: "Error de conexión")
            }
        }
    }

    class Factory(
        private val solicitudRepository: SolicitudRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdoptanteSolicitudesViewModel::class.java)) {
                return AdoptanteSolicitudesViewModel(solicitudRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
