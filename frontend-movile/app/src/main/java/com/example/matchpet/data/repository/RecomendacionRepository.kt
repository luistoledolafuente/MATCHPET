package com.example.matchpet.data.repository

import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.data.network.ApiService

class RecomendacionRepository(private val apiService: ApiService) {

    suspend fun getRecomendaciones(token: String): Result<List<Animal>> {
        var attempt = 0
        var lastError: Exception? = null
        while (attempt < 3) {
            try {
                val response = apiService.getRecomendaciones("Bearer $token")
                if (response.isSuccessful) {
                    return Result.success(response.body() ?: emptyList())
                } else {
                    lastError = Exception("Error ${response.code()} al obtener recomendaciones")
                }
            } catch (e: Exception) {
                lastError = e
            }
            attempt++
            kotlinx.coroutines.delay((attempt * 600L))
        }
        return Result.failure(lastError ?: Exception("Error desconocido al obtener recomendaciones"))
    }
}
