package com.example.matchpet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchpet.data.model.*
import com.example.matchpet.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import android.util.Log
import kotlinx.coroutines.withContext


class AuthViewModel : ViewModel() {

    var authResponse: AuthResponse? = null
        private set
    fun login(
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = RetrofitClient.api.login(request)

                if (response.isSuccessful && response.body() != null) {
                    authResponse = response.body()
                    onSuccess(response.body()!!)
                } else {
                    onError("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error de conexión")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        nombre: String,
        telefono: String?,
        onSuccess: (AuthResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    nombreCompleto = nombre,
                    email = email,
                    password = password,
                    telefono = telefono,
                    ciudad = "Lima",
                    direccion = "Av. Los Álamos 123",
                    rol = "ADOPTANTE"
                )

                val response: Response<AuthResponse> =
                    RetrofitClient.api.register(request)

                if (response.isSuccessful && response.body() != null) {
                    authResponse = response.body()

                    // 🔴 HAY QUE VOLVER AL HILO PRINCIPAL AQUÍ
                    withContext(Dispatchers.Main) {
                        onSuccess(response.body()!!)
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        onError("Error ${response.code()}: ${response.message()}")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Error de conexión")
                }
            }
        }
    }
}