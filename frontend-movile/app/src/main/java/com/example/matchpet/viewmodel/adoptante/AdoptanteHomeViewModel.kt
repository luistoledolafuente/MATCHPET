package com.example.matchpet.viewmodel.adoptante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.animal.Animal
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

data class DashboardData(
    val stats: DashboardStats,
    val mascotasRecomendadas: List<Animal>,
    val mascotasSolicitadas: List<Animal>
)

sealed class DashboardState {
    object Idle : DashboardState()
    object Loading : DashboardState()
    data class Success(val data: DashboardData) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class AdoptanteHomeViewModel : ViewModel() {

    private val _dashboardState = MutableStateFlow<DashboardState>(DashboardState.Idle)
    val dashboardState: StateFlow<DashboardState> = _dashboardState.asStateFlow()

    fun loadDashboardStats(token: String) {
        viewModelScope.launch {
            _dashboardState.value = DashboardState.Loading
            try {
                // 1. Obtener solicitudes del usuario
                val solicitudesResponse = RetrofitClient.api.getMisSolicitudes("Bearer $token")
                if (!solicitudesResponse.isSuccessful) {
                    _dashboardState.value = DashboardState.Error("Error al cargar solicitudes")
                    return@launch
                }
                
                val solicitudes = solicitudesResponse.body() ?: emptyList()
                
                // Contar estadísticas
                val pendientes = solicitudes.count { 
                    it.estadoSolicitud.nombre.uppercase() in listOf("PENDIENTE", "ENVIADA", "EN REVISIÓN")
                }
                
                val aprobadas = solicitudes.count { 
                    it.estadoSolicitud.nombre.uppercase() in listOf("APROBADA", "APROBADO")
                }
                
                val stats = DashboardStats(
                    solicitudesPendientes = pendientes,
                    adopcionesAprobadas = aprobadas,
                    totalDonaciones = 0
                )
                
                // 2. Obtener todas las mascotas disponibles
                val animalesResponse = RetrofitClient.api.getAnimales(page = 0, size = 50)
                val todasMascotas = animalesResponse.body()?.content ?: emptyList()
                
                // 3. Tomar 3 mascotas aleatorias
                val mascotasRecomendadas = todasMascotas.shuffled().take(3)
                
                // 4. Obtener las mascotas que el usuario ha solicitado
                val mascotasSolicitadas = solicitudes.map { solicitud ->
                    // Convertir AnimalSummary a Animal (necesitamos crear un objeto Animal desde los datos de la solicitud)
                    Animal(
                        animal_id = solicitud.animal.id,
                        nombre = solicitud.animal.nombre,
                        raza = null,
                        genero = null,
                        edad = null,
                        descripcionPersonalidad = null,
                        estadoAdopcion = "",
                        refugioNombre = null,
                        refugioCiudad = null,
                        fotos = solicitud.animal.fotos
                    )
                }.take(6) // Mostrar máximo 6 solicitudes
                
                _dashboardState.value = DashboardState.Success(
                    DashboardData(
                        stats = stats,
                        mascotasRecomendadas = mascotasRecomendadas,
                        mascotasSolicitadas = mascotasSolicitadas
                    )
                )
                
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
