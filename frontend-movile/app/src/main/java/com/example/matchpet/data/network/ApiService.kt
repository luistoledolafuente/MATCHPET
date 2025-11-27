package com.example.matchpet.data.network

import com.example.matchpet.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/adoptantes/register")
    suspend fun registerAdoptante(@Body request: AdopterRegisterRequest): Response<AuthResponse>

    @POST("/api/refugios/register")
    suspend fun registerRefugio(@Body request: ShelterRegisterRequest): Response<AuthResponse>


    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // 🔑 El token debe incluir el prefijo 'Bearer '
    @GET("/api/user/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfileResponse>

}