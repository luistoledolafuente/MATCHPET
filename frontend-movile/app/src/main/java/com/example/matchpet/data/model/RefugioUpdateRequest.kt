package com.example.matchpet.data.model

data class RefugioUpdateRequest(
    val nombre: String,
    val descripcion: String,
    val pais: String,
    val urlSitioWeb: String?,
    val direccion: String,
    val ciudad: String,
    val email: String,
    val personaContacto: String?,
    val telefono: String?
)
