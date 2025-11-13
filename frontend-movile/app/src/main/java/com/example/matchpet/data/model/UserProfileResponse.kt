package com.example.matchpet.data.model

data class UserProfileResponse(
    val usuarioId: Int,
    val nombreCompleto: String,
    val email: String,
    val telefono: String?,
    val role: String
)
