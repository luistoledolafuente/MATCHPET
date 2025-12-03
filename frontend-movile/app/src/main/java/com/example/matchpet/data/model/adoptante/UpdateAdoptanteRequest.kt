package com.example.matchpet.data.model.adoptante

data class UpdateAdoptanteRequest(
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val telefono: String,
    val fechaNacimiento: String, // Formato YYYY-MM-DD
    val direccion: String,
    val ciudad: String,
    val pais: String
)