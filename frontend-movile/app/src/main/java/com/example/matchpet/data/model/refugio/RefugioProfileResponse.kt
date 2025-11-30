package com.example.matchpet.data.model.refugio

data class RefugioProfileResponse(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val direccion: String,
    val ciudad: String,
    val pais: String,
    val email: String,
    val telefono: String?,
    val personaContacto: String?,
    val urlSitioWeb: String?
)