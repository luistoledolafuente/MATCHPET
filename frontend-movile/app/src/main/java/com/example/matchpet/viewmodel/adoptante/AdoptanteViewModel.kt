package com.example.matchpet.viewmodel.adoptante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log

// Modelo simplificado para la UI del Drawer (solo nombre y email)
data class SimpleAdoptanteUser(val name: String, val email: String)

class AdoptanteDashboardViewModel : ViewModel() {

    // StateFlow simple para el nombre/email de la cabecera del drawer
    private val _simpleUser = MutableStateFlow<SimpleAdoptanteUser?>(null)
    val simpleUser: StateFlow<SimpleAdoptanteUser?> = _simpleUser.asStateFlow()

    fun loadUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val authHeader = "Bearer $token"
                // Llamamos a getProfile (que devuelve UserProfileResponse)
                val response = RetrofitClient.api.getProfile(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!

                    // Extraemos los campos necesarios de UserProfileResponse
                    _simpleUser.value = SimpleAdoptanteUser(
                        name = profile.nombre ?: "Adoptante sin Nombre",
                        email = profile.email
                    )
                } else {
                    Log.e("AdoptanteDashVM", "Error cargando perfil: ${response.code()}")
                    setFallbackUser("Error al cargar perfil")
                }
            } catch (e: Exception) {
                Log.e("AdoptanteDashVM", "Exception cargando perfil", e)
                setFallbackUser("Error de conexión")
            }
        }
    }

    private fun setFallbackUser(nombreError: String) {
        _simpleUser.value = SimpleAdoptanteUser(name = nombreError, email = "N/A")
    }

    // Alias para mantener compatibilidad
    fun loadUser(token: String) {
        loadUserProfile(token)
    }
}
