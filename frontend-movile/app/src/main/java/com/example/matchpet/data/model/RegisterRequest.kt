package com.example.matchpet.data.model

data class RegisterRequest(
    val nombreCompleto: String,
    val email: String,
    val password: String,
    val telefono: String?,
    val ciudad: String,
    val direccion: String,
    val rol: String
)
