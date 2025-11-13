package com.example.matchpet.data.network

import com.example.matchpet.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("/api/user/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>
}
