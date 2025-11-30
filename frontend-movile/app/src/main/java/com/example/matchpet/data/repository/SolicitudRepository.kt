package com.example.matchpet.data.repository

import com.example.matchpet.data.model.SolicitudResponse
import com.example.matchpet.data.model.SolicitudUpdateRequest
import com.example.matchpet.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SolicitudRepository(private val apiService: ApiService) {

    suspend fun getSolicitudesRecibidas(token: String): Resource<List<SolicitudResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getSolicitudesRecibidas("Bearer $token")
                if (response.isSuccessful) {
                    Resource.Success(response.body() ?: emptyList())
                } else {
                    Resource.Error("Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    suspend fun updateSolicitudStatus(
        token: String,
        solicitudId: Int,
        nuevoEstadoId: Int,
        notas: String? = null,
        mensaje: String? = null
    ): Resource<SolicitudResponse> {

        val request = SolicitudUpdateRequest(
            estadoSolicitudId = nuevoEstadoId,
            notasInternas = notas,
            mensajeAlAdoptante = mensaje
        )

        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.actualizarEstadoSolicitud(
                    "Bearer $token",
                    solicitudId,
                    request
                )

                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }
}
