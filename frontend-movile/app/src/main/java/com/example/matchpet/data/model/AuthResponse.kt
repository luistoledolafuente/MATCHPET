package com.example.matchpet.data.model

// **Corregir AuthResponse para que coincida con tu endpoint /api/auth/login**
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
    // El backend no devuelve 'token_type' ni 'user' en el login según tu swagger.
)

// **Eliminar UserResponse.kt si lo tenías, ya que el login no lo devuelve**
// La info del usuario la obtendremos con /api/user/profile.
