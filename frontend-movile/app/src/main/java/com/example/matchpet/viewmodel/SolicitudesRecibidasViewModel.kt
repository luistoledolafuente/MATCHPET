package com.example.matchpet.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.animal.LookupItem
import com.example.matchpet.data.model.SolicitudResponse
import com.example.matchpet.data.repository.Resource
import com.example.matchpet.data.repository.SolicitudRepository

import com.example.matchpet.data.model.animal.AnimalUpdateRequest
import com.example.matchpet.data.network.RetrofitClient
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
            // 1. Actualizar el estado de la solicitud
            when (solicitudRepository.updateSolicitudStatus(token, solicitudId, nuevoEstadoId)) {
                is Resource.Success -> {
                    // 2. Actualizar el estado de la mascota según el estado de la solicitud
                    val solicitud = _solicitudes.value.find { it.id == solicitudId }
                    if (solicitud != null) {
                        try {
                            val estadosResp = RetrofitClient.api.getEstadosAdopcion()
                            if (estadosResp.isSuccessful) {
                                val estadosAdopcion = estadosResp.body() ?: emptyList()
                                
                                // Mapeo de estados
                                // En revisión (2) -> En proceso (2)
                                // Aprobada (3) -> Adoptado (3)
                                // Rechazada (4) -> Disponible (1)
                                
                                val nuevoEstadoAnimalId = when (nuevoEstadoId) {
                                    2 -> estadosAdopcion.firstOrNull { it.nombre.equals("En proceso", true) }?.id
                                    3 -> estadosAdopcion.firstOrNull { it.nombre.equals("Adoptado", true) }?.id
                                    4 -> estadosAdopcion.firstOrNull { it.nombre.equals("Disponible", true) }?.id
                                    else -> null
                                }

                                if (nuevoEstadoAnimalId != null) {
                                    RetrofitClient.api.updateAnimal(
                                        id = solicitud.animal.id,
                                        token = "Bearer $token",
                                        request = AnimalUpdateRequest(estadoAdopcionId = nuevoEstadoAnimalId)
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            // Manejar error silenciosamente o loguear
                        }
                    }
                    loadSolicitudes(token)
                }
                is Resource.Error -> _state.value =
                    SolicitudesRecibidasState.Error("No se pudo actualizar el estado.")
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
