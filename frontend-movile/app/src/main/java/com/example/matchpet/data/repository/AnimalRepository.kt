package com.example.matchpet.data.repository

import com.example.matchpet.data.model.AnimalResponse
import com.example.matchpet.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Asumiendo que Resource ya está definida como en tus otros archivos
// sealed class Resource<out T> { ... }

class AnimalRepository(private val apiService: ApiService) {
    fun getMisAnimales(token: String): Flow<Resource<List<AnimalResponse>>> = flow {
        emit(Resource.Loading())
        try {
            // El endpoint /mis-animales devuelve una lista directa de AnimalResponse/AnimalDTO
            val response = apiService.getMisAnimales("Bearer $token")
            if (response.isSuccessful) {
                // El cuerpo puede ser List<AnimalResponse> o nulo. Si es nulo, devolvemos lista vacía.
                val animales = response.body() ?: emptyList()
                emit(Resource.Success(animales))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                // Aquí puedes implementar una lógica de parseo de error más detallada si es necesario
                emit(Resource.Error("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Fallo en la red o conversión de datos: ${e.localizedMessage}"))
        }
    }
}