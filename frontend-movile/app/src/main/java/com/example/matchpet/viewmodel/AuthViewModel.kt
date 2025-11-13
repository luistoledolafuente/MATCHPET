package com.example.matchpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.*
import com.example.matchpet.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel : ViewModel() {

    var authResponse: AuthResponse? = null
        private set

    /**
     * Registro de usuario
     */
    fun register(
        email: String,
        password: String,
        nombre: String,
        telefono: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.api.register(
                    RegisterRequest(email, password, nombre, telefono)
                )
                if (response.isSuccessful && response.body() != null) {
                    authResponse = response.body()
                    onSuccess()
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error al registrarse")
            }
        }

    }

    /**
     * Login de usuario
     */
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("📤 Iniciando sesión: $email")

                val response = RetrofitClient.api.login(LoginRequest(email, password))

                println("📥 Respuesta login: ${response.code()} ${response.message()}")

                if (response.isSuccessful && response.body() != null) {
                    authResponse = response.body()
                    println("✅ Login exitoso: ${authResponse?.accessToken}")
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    println("⚠️ Error en login: $errorBody")
                    onError("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: HttpException) {
                println("❌ HttpException: ${e.code()} - ${e.message()}")
                onError("Error HTTP ${e.code()}")
            } catch (e: Exception) {
                println("❌ Excepción en login: ${e.localizedMessage}")
                onError("Excepción: ${e.localizedMessage ?: "Error de red"}")
            }
        }
    }
}
