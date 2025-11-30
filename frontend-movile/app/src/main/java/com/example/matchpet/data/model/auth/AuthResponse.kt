package com.example.matchpet.data.model.auth

// **Corregir AuthResponse para que coincida con tu endpoint /api/auth/login**
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
    // El backend no devuelve 'token_type' ni 'user' en el login según tu swagger.
)