package com.example.matchpet.viewmodel.adoptante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardStats(
    val solicitudesPendientes: Int = 0,
    val adopcionesAprobadas: Int = 0,
    val totalDonaciones: Int = 0,
    val totalFavoritos: Int = 0
)

data class DashboardData(
    val stats: DashboardStats,
    val mascotasRecomendadas: List<Animal>,
    val mascotasSolicitadas: List<Animal>,
    val solicitudesPendientesAnimales: List<Animal>
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

    // Estado para el buscador
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allPets = MutableStateFlow<List<Animal>>(emptyList())

    // Resultados de la búsqueda (filtrados)
    val searchResults: StateFlow<List<Animal>> = combine(_allPets, _searchQuery) { pets, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            pets.filter { animal ->
                animal.nombre.contains(query, ignoreCase = true) ||
                (animal.raza?.contains(query, ignoreCase = true) == true) ||
                (animal.refugioNombre?.contains(query, ignoreCase = true) == true)
            }
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

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
                
                // Obtener cantidad de favoritos
                val favoritosResponse = RetrofitClient.api.getFavorites("Bearer $token")
                val totalFavoritos = if (favoritosResponse.isSuccessful) {
                    favoritosResponse.body()?.size ?: 0
                } else {
                    0
                }
                
                val stats = DashboardStats(
                    solicitudesPendientes = pendientes,
                    adopcionesAprobadas = aprobadas,
                    totalDonaciones = 0,
                    totalFavoritos = totalFavoritos
                )
                
                // 2. Obtener todas las mascotas disponibles
                val animalesResponse = RetrofitClient.api.getAnimales(page = 0, size = 50)
                val todasMascotas = (animalesResponse.body()?.content ?: emptyList())
                    .filter { 
                        it.estadoAdopcion.equals("Disponible", ignoreCase = true) || 
                        it.estadoAdopcion.equals("En proceso", ignoreCase = true)
                    }
                _allPets.value = todasMascotas
                
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

                val pendientesAnimales = solicitudes.filter {
                    it.estadoSolicitud.nombre.uppercase() in listOf("PENDIENTE", "ENVIADA", "EN REVISIÓN")
                }.map { solicitud ->
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
                }.take(6)
                
                _dashboardState.value = DashboardState.Success(
                    DashboardData(
                        stats = stats,
                        mascotasRecomendadas = mascotasRecomendadas,
                        mascotasSolicitadas = mascotasSolicitadas,
                        solicitudesPendientesAnimales = pendientesAnimales
                    )
                )
                
            } catch (e: Exception) {
                _dashboardState.value = DashboardState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
