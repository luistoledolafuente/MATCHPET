package com.example.matchpet.data.model.auth

enum class UserRole(val displayName: String) {
    ADOPTER("Adoptante"),
    SHELTER("Refugio");

    companion object {
        // ✅ CORRECCIÓN: Aceptar String? (nulo) y manejarlo.
        fun fromString(value: String?): UserRole {
            return when (value?.uppercase()) { // Usamos ?.uppercase() para seguridad contra null
                "ADOPTER", "ADOPTANTE" -> ADOPTER
                "SHELTER", "REFUGIO" -> SHELTER
                // 💡 Si el valor es null o desconocido, lanzamos la excepción
                else -> throw IllegalArgumentException("Unknown or null role: $value")
            }
        }
    }
}