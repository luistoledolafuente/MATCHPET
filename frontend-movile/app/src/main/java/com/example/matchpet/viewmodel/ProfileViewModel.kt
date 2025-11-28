package com.example.matchpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.UserProfileResponse // 🔑 Importar el modelo REAL de la respuesta
import com.example.matchpet.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import android.util.Log

// Modelo simplificado para la UI del Drawer y Home (solo nombre y email)
data class SimpleRefugioUser(val name: String, val email: String)

// Modelos simplificados para el dashboard
data class Animal(val nombre: String, val raza: String?, val fechaCreacion: Date = Date())
data class BitacoraItem(val descripcion: String, val fecha: Date)


class ProfileViewModel : ViewModel() {

    // 🔑 NUEVO: StateFlow simple para el nombre/email de la cabecera
    private val _simpleUser = MutableStateFlow<SimpleRefugioUser?>(null)
    val simpleUser: StateFlow<SimpleRefugioUser?> = _simpleUser.asStateFlow()

    private val _animales = MutableStateFlow<List<Animal>>(emptyList())
    val animales: StateFlow<List<Animal>> = _animales.asStateFlow()

    private val _bitacora = MutableStateFlow<List<BitacoraItem>>(emptyList())
    val bitacora: StateFlow<List<BitacoraItem>> = _bitacora.asStateFlow()

    fun loadUserProfile(token: String) {
        viewModelScope.launch {
            try {
                val authHeader = "Bearer $token"
                // 🔑 Llamamos a getProfile (que devuelve UserProfileResponse)
                val response = RetrofitClient.api.getProfile(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!

                    // 🔑 CORRECTO: Extraemos los campos necesarios de UserProfileResponse
                    _simpleUser.value = SimpleRefugioUser(
                        name = profile.nombre ?: "Refugio sin Nombre", // Usamos 'nombre' del UserProfileResponse
                        email = profile.email
                    )
                } else {
                    Log.e("RefugioDashVM", "Error cargando perfil: ${response.code()}")
                    setFallbackUser("Error al cargar perfil")
                }
            } catch (e: Exception) {
                Log.e("RefugioDashVM", "Exception cargando perfil", e)
                setFallbackUser("Error de conexión")
            }
        }
    }

    private fun setFallbackUser(nombreError: String) {
        _simpleUser.value = SimpleRefugioUser(name = nombreError, email = "N/A")
    }



    // Función original renombrada (antes loadUser) para ser llamada desde HomeScreen
    fun loadUser(token: String) {
        loadUserProfile(token)
    }

    // Manteniendo el resto de las funciones:
    fun loadAnimales(token: String) {
        viewModelScope.launch {
            _animales.value = listOf(
                Animal("Max", "Perro", Date(System.currentTimeMillis() - 86400000)), // Ayer
                Animal("Luna", "Gato", Date()), // Hoy
            )
        }
    }

    fun loadBitacora(token: String) {
        viewModelScope.launch {
            _bitacora.value = listOf(
                BitacoraItem("Max fue adoptado", Date(System.currentTimeMillis() - 3600000)),
                BitacoraItem("Nueva mascota agregada: Luna", Date()),
            )
        }
    }
}
