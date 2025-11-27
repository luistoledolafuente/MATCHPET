package com.example.matchpet.data.repository

import com.example.matchpet.data.model.UserProfileResponse
import com.example.matchpet.data.model.RefugioProfileResponse
import com.example.matchpet.data.model.RefugioUpdateRequest
import com.example.matchpet.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// -------------------------------------------------------------
// Resource<T> - Manejo estándar de Estados
// -------------------------------------------------------------
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

// -------------------------------------------------------------
// REPOSITORIO EXCLUSIVO PARA REFUGIO
// -------------------------------------------------------------
class RefugioRepository(
    private val apiService: ApiService
) {

    // 1. Obtener perfil del usuario (que incluye los datos del Refugio)
    fun getProfile(token: String): Flow<Resource<UserProfileResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = apiService.getProfile("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                emit(Resource.Error("Fallo al cargar perfil: $errorMsg"))
            }

        } catch (e: Exception) {
            emit(Resource.Error("Error de conexión: ${e.localizedMessage}"))
        }
    }

    // 2. Actualizar datos del perfil del refugio
    suspend fun updateRefugioProfile(
        id: Int,
        request: RefugioUpdateRequest,
        token: String
    ): Resource<RefugioProfileResponse> {

        return try {
            val response = apiService.updateRefugioProfile(id, "Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error ${response.code()}"
                Resource.Error("Fallo al actualizar Refugio: $errorMsg")
            }

        } catch (e: Exception) {
            Resource.Error("Error de red: ${e.localizedMessage}")
        }
    }
}
