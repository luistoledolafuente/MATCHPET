package com.example.matchpet.data.model

data class UserProfileResponse(
    val usuarioId: Int,
    val nombreCompleto: String,
    val email: String,
    val telefono: String?,
    // ✅ CORRECCIÓN: Ahora se espera una lista llamada 'roles'
    val roles: List<String>,
    // Otros campos
)