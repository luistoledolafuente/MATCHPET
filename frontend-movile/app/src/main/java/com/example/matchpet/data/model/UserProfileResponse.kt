package com.example.matchpet.data.model

data class UserProfileResponse(
    val usuarioId: Int,
    val email: String,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val telefono: String?,
    val fechaNacimiento: String,
    val direccion: String?,
    val ciudad: String?,
    val pais: String?,
    val roles: List<String>
    // Eliminado: refugio, porque NO existe RefugioData
)
