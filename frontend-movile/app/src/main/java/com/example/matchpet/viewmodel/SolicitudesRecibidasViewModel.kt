package com.example.matchpet.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.animal.LookupItem
import com.example.matchpet.data.model.SolicitudResponse
import com.example.matchpet.data.repository.Resource
import com.example.matchpet.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SolicitudesRecibidasState {
    object Idle : SolicitudesRecibidasState()
    object Loading : SolicitudesRecibidasState()
    data class Error(val message: String) : SolicitudesRecibidasState()
    object Success : SolicitudesRecibidasState()
}

class SolicitudesRecibidasViewModel(
    private val solicitudRepository: SolicitudRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SolicitudesRecibidasState>(SolicitudesRecibidasState.Idle)
    val state: StateFlow<SolicitudesRecibidasState> = _state.asStateFlow()

    private val _solicitudes = MutableStateFlow<List<SolicitudResponse>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudResponse>> = _solicitudes.asStateFlow()

    val estadosDisponibles = mutableStateOf(
        listOf(
            LookupItem(1, "Pendiente"),
            LookupItem(2, "Aprobada"),
            LookupItem(3, "Rechazada")
        )
    )

    fun loadSolicitudes(token: String) {
        _state.value = SolicitudesRecibidasState.Loading
        viewModelScope.launch {
            when (val result = solicitudRepository.getSolicitudesRecibidas(token)) {
                is Resource.Success -> {
                    _solicitudes.value = result.data ?: emptyList()
                    _state.value = SolicitudesRecibidasState.Success
                }
                is Resource.Error -> {
                    _state.value = SolicitudesRecibidasState.Error(result.message ?: "Error desconocido.")
                }
                else -> {}
            }
        }
    }

    fun updateSolicitudStatus(token: String, solicitudId: Int, nuevoEstadoId: Int) {
        viewModelScope.launch {
            when (solicitudRepository.updateSolicitudStatus(token, solicitudId, nuevoEstadoId)) {
                is Resource.Success -> loadSolicitudes(token)
                is Resource.Error -> _state.value =
                    SolicitudesRecibidasState.Error("No se pudo actualizar.")
                else -> {}
            }
        }
    }

    class Factory(private val repository: SolicitudRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SolicitudesRecibidasViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SolicitudesRecibidasViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
