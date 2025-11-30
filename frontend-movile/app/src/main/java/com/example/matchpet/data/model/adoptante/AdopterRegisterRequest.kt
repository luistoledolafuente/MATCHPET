package com.example.matchpet.data.model.adoptante

data class AdopterRegisterRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val telefono: String,
    val fechaNacimiento: String, // Usaremos String (YYYY-MM-DD)
    val direccion: String,
    val ciudad: String,
    val pais: String
)