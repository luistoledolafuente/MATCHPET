package com.example.matchpet.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)


