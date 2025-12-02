package com.example.matchpet.viewmodel.adoptante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardStats(
    val solicitudesPendientes: Int = 0,
    val adopcionesAprobadas: Int = 0,
    val totalDonaciones: Int = 0
)

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    data class Success(val stats: DashboardStats) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class AdoptanteHomeViewModel : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    fun loadDashboardStats(token: String) {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                val response = RetrofitClient.api.getMisSolicitudes("Bearer $token")
                if (response.isSuccessful) {
                    val solicitudes = response.body() ?: emptyList()
                    
                    // Contar solicitudes pendientes
                    val pendientes = solicitudes.count { 
                        it.estadoSolicitud.nombre.uppercase() in listOf("PENDIENTE", "ENVIADA", "EN REVISIÓN")
                    }
                    
                    // Contar adopciones aprobadas
                    val aprobadas = solicitudes.count { 
                        it.estadoSolicitud.nombre.uppercase() in listOf("APROBADA", "APROBADO")
                    }
                    
                    // Donaciones por ahora en 0 (no hay endpoint)
                    val donaciones = 0
                    
                    _dashboardState.value = DashboardState.Success(
                        DashboardStats(
                            solicitudesPendientes = pendientes,
                            adopcionesAprobadas = aprobadas,
                            totalDonaciones = donaciones
                        )
                    )
                } else {
                    _dashboardState.value = DashboardState.Error("Error al cargar estadísticas: ${response.message()}")
                }
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
