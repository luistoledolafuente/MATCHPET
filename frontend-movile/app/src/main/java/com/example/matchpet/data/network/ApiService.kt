package com.example.matchpet.data.network

import com.example.matchpet.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/adoptantes/register")
    suspend fun registerAdoptante(@Body request: AdopterRegisterRequest): Response<AuthResponse>

    @POST("/api/refugios/register")
    suspend fun registerRefugio(@Body request: ShelterRegisterRequest): Response<AuthResponse>

    // 1. Actualización de perfil del Refugio
    @PUT("/api/refugios/{id}")
    suspend fun updateRefugioProfile(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body request: RefugioUpdateRequest
    ): Response<RefugioProfileResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // 🔑 El token debe incluir el prefijo 'Bearer '
    @GET("/api/user/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfileResponse>


}