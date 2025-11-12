package com.example.matchpet.data.model

data class ShelterProfile(
    val usuarioId: Int,
    val refugioId: Int,
    val nombreRefugio: String,
    val direccion: String,
    val ciudad: String,
    val telefono: String?,
    val email: String,
    val personaContacto: String?
)