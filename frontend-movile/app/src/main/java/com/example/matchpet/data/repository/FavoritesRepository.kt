package com.example.matchpet.data.repository

import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.data.network.ApiService
import retrofit2.Response

class FavoritesRepository(private val apiService: ApiService) {

    suspend fun getFavorites(token: String): Result<List<Animal>> {
        return try {
            val response = apiService.getFavorites("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                if (response.code() == 404) {
                    Result.success(emptyList())
                } else {
                    Result.failure(Exception("Error al obtener favoritos: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(token: String, animalId: Int): Result<Boolean> {
        return try {
            val response = apiService.addFavorite("Bearer $token", animalId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al agregar favorito: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(token: String, animalId: Int): Result<Boolean> {
        return try {
            val response = apiService.removeFavorite("Bearer $token", animalId)
            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al eliminar favorito: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
