package com.example.matchpet.data.model

data class User(
    val usuarioId: Int,
    val email: String,
    val nombreCompleto: String,
    val telefono: String?,
    val role: UserRole,
    val estaActivo: Boolean = true
)
