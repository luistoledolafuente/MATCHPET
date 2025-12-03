package com.example.matchpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.*
import com.example.matchpet.utils.Injection // Importamos Injection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import com.example.matchpet.data.model.adoptante.AdopterRegisterRequest
import com.example.matchpet.data.model.auth.LoginRequest
import com.example.matchpet.data.model.auth.UserRole
import com.example.matchpet.data.model.refugio.ShelterRegisterRequest

// Mejorar la gestión de estado con una clase sellada
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String, val userRole: UserRole) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    // Usaremos LiveData/StateFlow para exponer el estado a la UI
    private val _authState = kotlinx.coroutines.flow.MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: kotlinx.coroutines.flow.StateFlow<AuthState> = _authState

    // Función auxiliar para obtener el perfil y determinar el rol
    private suspend fun fetchProfileAndNavigate(token: String) {
        val authHeader = "Bearer $token"
        // 🔑 CAMBIO: Usar Injection.apiService en lugar de RetrofitClient.api
        val profileResponse = Injection.apiService.getProfile(authHeader)

        if (profileResponse.isSuccessful && profileResponse.body() != null) {
            val profile = profileResponse.body()!!
            try {
                // ✅ CORRECCIÓN: Intentamos obtener el primer rol de la lista 'roles'
                val roleString = profile.roles.firstOrNull()

                // Usamos la función fromString que ya hicimos segura contra nulos
                val role = UserRole.fromString(roleString)

                _authState.value = AuthState.Success(token, role)
            } catch (e: Exception) { // Capturamos IllegalArgumentException u otras excepciones
                val receivedRole = profile.roles.joinToString(", ") // Para mejor depuración
                Log.e("AuthViewModel", "Error al procesar rol: $receivedRole", e)
                _authState.value = AuthState.Error("Error al procesar el rol recibido: $receivedRole. ${e.message}")
            }
        } else {
            _authState.value = AuthState.Error("Login/Registro exitoso, pero error al obtener perfil: ${profileResponse.code()}")
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = LoginRequest(email = email, password = password)
                // 🔑 CAMBIO: Usar Injection.apiService
                val response = Injection.apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.accessToken
                    fetchProfileAndNavigate(token) // Obtener perfil después del login
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    _authState.value = AuthState.Error("Error ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login Exception", e)
                _authState.value = AuthState.Error(e.message ?: "Error de conexión/Login")
            }
        }
    }

    // Adaptamos el registro para Adoptante
    fun registerAdoptante(
        email: String,
        password: String,
        nombre: String,
        telefono: String,
        // Agrega todos los campos faltantes requeridos por tu nuevo modelo
        apellidoPaterno: String,
        apellidoMaterno: String,
        fechaNacimiento: String, // Formato YYYY-MM-DD
        direccion: String,
        ciudad: String,
        pais: String
    ) {
        _authState.value = AuthState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = AdopterRegisterRequest(
                    email = email,
                    password = password,
                    nombre = nombre,
                    apellidoPaterno = apellidoPaterno,
                    apellidoMaterno = apellidoMaterno,
                    telefono = telefono,
                    fechaNacimiento = fechaNacimiento,
                    direccion = direccion,
                    ciudad = ciudad,
                    pais = pais
                )

                // 🔑 CAMBIO: Usar Injection.apiService
                val response = Injection.apiService.registerAdoptante(request)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.accessToken
                    fetchProfileAndNavigate(token) // Obtener perfil después del registro
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    _authState.value = AuthState.Error("Error ${response.code()}: $errorBody")
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Register Exception", e)
                _authState.value = AuthState.Error(e.message ?: "Error de conexión/Registro")
            }
        }
    }
    fun registerRefugio(
        emailLogin: String,
        password: String,
        nombreRefugio: String,
        descripcion: String,
        direccion: String,
        ciudad: String,
        pais: String,
        emailRefugio: String,
        personaContacto: String,
        telefonoContacto: String,
        urlSitioWeb: String? = null
    ) {
        _authState.value = AuthState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ShelterRegisterRequest(
                    emailLogin = emailLogin,
                    password = password,
                    nombreRefugio = nombreRefugio,
                    descripcion = descripcion,
                    direccion = direccion,
                    ciudad = ciudad,
                    pais = pais,
                    emailRefugio = emailRefugio,
                    personaContacto = personaContacto,
                    telefonoContacto = telefonoContacto,
                    urlSitioWeb = urlSitioWeb
                )

                // 🔑 CAMBIO: Usar Injection.apiService
                val response = Injection.apiService.registerRefugio(request)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.accessToken
                    fetchProfileAndNavigate(token) // Obtener perfil después del registro
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                    _authState.value = AuthState.Error("Error ${response.code()}: $errorBody")
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Register Refugio Exception", e)
                _authState.value = AuthState.Error(e.message ?: "Error de conexión/Registro de Refugio")
            }
        }
    }


    fun resetState() {
        _authState.value = AuthState.Idle
    }
}