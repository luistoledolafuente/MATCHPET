package com.example.matchpet.data.model

data class AuthResponse(
    val accessToken: String,
    val token_type: String,
    val refreshToken: String,
    val user: UserResponse
)

data class UserResponse(
    val id: Long,
    val nombreCompleto: String,
    val email: String,
    val telefono: String?,
    val rol: String
)
